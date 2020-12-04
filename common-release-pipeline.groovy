/***************************************************************/
/*********************** JENKINS-RELEASE LIB ***********************/
/***************************************************************/
// MAINTAINER:          lukas.feuz@post.ch, matthias.baldi@post.ch
// DOCUMENTATION:       README.md
// GIT SCM URL:         https://gitit.post.ch/projects/JENKINS/repos/common-pipeline/browse
    
sh "wget -O ./jenkins-build-pipeline.groovy http://artifactory.pnet.ch/artifactory/libs-release-local/ch/post/it/common/jenkins/pipeline/00.02.01.00/pipeline-00.02.01.00.jar!jenkins-build-pipeline.groovy"
commonBuildPipeline = load 'jenkins-build-pipeline.groovy'

public performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion) {
    performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, null, null, false)
}

public performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, newSnapshotVersion) {
    performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, newSnapshotVersion, null, false)
}

public performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, newSnapshotVersion, releaseEmail) {
    performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, newSnapshotVersion, releaseEmail, false)
}

public performRelease(projectName, gitUrl, branchName, autoMerge, mavenGoals, mavenParameters, workspace, releaseVersion, newSnapshotVersion, releaseEmail, useArtifactoryArtifactUploader) {

    // cancel release build if no release version is given
    if(releaseVersion.length() < 1) {
        error "no release version was given - please set this parameter!"
    }

    // calculate snapshot version if not given
    if (!newSnapshotVersion) {
        newSnapshotVersion = getNewSnapshotVersion(releaseVersion)
    }

    // log given parameters
    commonBuildPipeline.LOGGER('automerge', autoMerge)
    commonBuildPipeline.LOGGER('branch name', branchName)
    commonBuildPipeline.LOGGER('release version', releaseVersion)
    commonBuildPipeline.LOGGER('snapshot version', newSnapshotVersion)

    // checkout and merge branches
    stage('checkout') {
        if(autoMerge == 'true') {
            this.commonBuildPipeline.checkoutSource(projectName, gitUrl, branchName)
            commonBuildPipeline.LOGGER('automerge', "automerge was enabled, we merge ${branchName} into master")
            mergeBranch(branchName)
        } else {
            commonBuildPipeline.LOGGER('automerge', "automerge was not enabled, we don't merge")
            this.commonBuildPipeline.checkoutSource(projectName, gitUrl, branchName)
        }
    }

    // run validity checks
    stage('update version') {
        checkAppVersionValidity(workspace, getNewSnapshotVersion(releaseVersion))
        commonBuildPipeline.LOGGER('service version', "set new service version ${releaseVersion} in pom!")
        sh "mvn ${commonBuildPipeline.MAVEN_POM_NAME} versions:set -DnewVersion=${releaseVersion}"
        sh "mvn ${commonBuildPipeline.MAVEN_POM_NAME} versions:commit"
    }

    // run release build
    stage('release') {
        RTMAVEN.deployer.deployArtifacts = useArtifactoryArtifactUploader
        // build maven command
        def mavenCommand = (mavenGoals != null ? mavenGoals + ' ' : '') + ' -DsnapshotDependencyAllowed=false -DupdateReleaseInfo=true -U ' + (mavenParameters != null ? ' ' + mavenParameters : '')
        if(commonBuildPipeline.MAVEN_POM_NAME.size() > 0) {
            commonBuildPipeline.RTMAVEN.run pom: commonBuildPipeline.MAVEN_POM_NAME.replace('-f ', ''), goals: "${mavenGoals}", buildInfo: commonBuildPipeline.BUILD_INFO
        } else {
            commonBuildPipeline.RTMAVEN.run pom: 'pom.xml', goals: mavenCommand, buildInfo: commonBuildPipeline.BUILD_INFO
        }

        tagAndCommit(projectName, gitUrl, releaseVersion)
        // save artifacts with build info
        saveArtifacts(projectName, branchName)
    }

    // add sonar stuff

    // set new snapshot version and run snapshot build
    stage('set/build snapshot') {
        if(autoMerge == 'true') {
            commonBuildPipeline.LOGGER('build snapshot', "automerge was enabled, we reset pom to Snapshot and rebuild on ${branchName}")
            resetPomVersion(projectName, workspace, gitUrl, branchName, releaseVersion, newSnapshotVersion)
            commonBuildPipeline.LOGGER("build", "current build status - ${currentBuild.result}")
            try {
                commonBuildPipeline.LOGGER("build", "start SNAPSHOT build - ${projectName}/${branchName}")
                build job: "${projectName}/${branchName}", propagate: false, wait: false
            } finally {
                commonBuildPipeline.LOGGER("build", "current build status - ${currentBuild.result}")
            }
        } else {
            commonBuildPipeline.LOGGER('build snapshot', "automerge was not enabled, we don't build a new Snapshot")
        }
        if (releaseEmail) {
            sendReleaseEmail(projectName, releaseVersion, releaseEmail)
        } else {
            commonBuildPipeline.LOGGER('release email', 'no email was given per parameter')
        }
    }
}

/***************************************************************/
/*************** HELPER METHODS FOR JENKINS-RELEASE ****************/
/***************************************************************/

// merge given branch
def mergeBranch(branchName) {
    commonBuildPipeline.LOGGER('checkout', "branch - ${branchName}, autoMerge - true")
    sh "git checkout master"
    sh "git merge -X theirs ${branchName}"
} 

// tag and commit into scm
def tagAndCommit(projectName, gitUrl, releaseVersion) {
    def pomName = "pom.xml"
    if(commonBuildPipeline.MAVEN_POM_NAME != '') {
        pomName = commonBuildPipeline.MAVEN_POM_NAME.replace('-f ', '')
    }
    // commit all changes and tag newest commit
    sh "git add --all"
    sh "git commit -m 'merge release ${projectName}-${releaseVersion}'"
    sh "git tag -af ${projectName}-${releaseVersion} -m 'tag by jenkins ci'"
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: USER_CREDENTIALS_SCONTINUUM_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS']]) {
        def url = gitUrl.replace('https://', '')
        sh "git push https://${GIT_USER}:${GIT_PASS}@${url} --all"
        sh "git push https://${GIT_USER}:${GIT_PASS}@${url} --tags"
    }
}

// reset pom to snapshotVersion
def resetPomVersion(projectName, workspace, gitUrl, branchName, releaseVersion, newSnapshotVersion) {
    def pomName = "pom.xml"
    if(commonBuildPipeline.MAVEN_POM_NAME != '') {
        pomName = commonBuildPipeline.MAVEN_POM_NAME.replace('-f ', '')
    }
    def project = readMavenPom file: "${workspace}/${pomName}"
    sh "git checkout ${branchName}"
    sh "mvn ${commonBuildPipeline.MAVEN_POM_NAME} versions:set -DnewVersion=${newSnapshotVersion}"
    sh "mvn ${commonBuildPipeline.MAVEN_POM_NAME} versions:commit"
    // commit into branch
    sh "git add --all"
    sh "git commit -m 'merge release ${projectName}-${releaseVersion}'"
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: USER_CREDENTIALS_SCONTINUUM_ID, usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS']]) {
        def url = gitUrl.replace('https://', '')
        sh "git push https://${GIT_USER}:${GIT_PASS}@${url} --all"
    }
}

// calculate new snapshot versions
def getNewSnapshotVersion(newversion) {
    def lastSegmentIndex = newversion.lastIndexOf(".") + 1
    def lastSegmentNumber = newversion.substring(lastSegmentIndex).toInteger()
    lastSegmentNumber++
    def snapshotversion = newversion.substring(0, lastSegmentIndex) + String.format('%02d', lastSegmentNumber) + "-SNAPSHOT"
    commonBuildPipeline.LOGGER('snapshot', "new snapshot number ${snapshotversion}")
    return snapshotversion
}

// check app version validity
def checkAppVersionValidity(workspace, newversion) {
    def project = readMavenPom file: "${workspace}/pom.xml"
    def oldversion = project.version
    def iNewVersion = newversion.replace( "-SNAPSHOT", "").replaceAll("\\.", "").toInteger()
    def iOldVersion = oldversion.replace( "-SNAPSHOT", "").replaceAll("\\.", "").toInteger()
    // new version must be greater than snapshot version
    commonBuildPipeline.LOGGER('service version', 'check app version')
    if ( iNewVersion < iOldVersion ) {
        error "version is not greater than the snapshot version (${newversion} < ${oldversion})"
    }
}

public sendReleaseEmail(projectName, releaseVersion, users) {
    // send release Email
    def emailBody = """A new release is deployed: <b>${projectName} - ${releaseVersion}</b><br>"""
    emailext (
        subject: "Release Notification: ${projectName} - ${releaseVersion}",
        body: """
            <p>${emailBody}</p>
            """,
        to: users
    )
}

// archive artifacts
public saveArtifacts(projectName, branchName) {
    stage('archive artifacts') {
        commonBuildPipeline.saveArtifacts(projectName, 'release', branchName)
    }
}

return this