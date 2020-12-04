/** *************************************************************/
/********************** JENKINS-PIPELINE LIB ***********************/
/** *************************************************************/
// MAINTAINER:          lukas.feuz@post.ch, matthias.baldi@post.ch
// DOCUMENTATION:       README.md
// GIT SCM URL:         https://gitit.post.ch/projects/JENKINS/repos/common-pipeline/browse

ARTIFACTORY_DOCKER_HOST = 'docker.pnet.ch'
USER_CREDENTIALS_SCONTINUUM_ID =  '32b1c695-9ddc-4114-a025-07fce287494c'
USER_CREDENTIALS_SJDOCKER_ID =  'f68e22a9-4d3d-4cbb-9c39-998164396bb7'
USER_CREDENTIALS_SJDEPLOY_ID =  '05e790a3-2dfd-4a30-b846-513dfcfa152f'
PROJECT_VERSION = null
PROJECT_BRANCH_VERSION = null
MAVEN_POM_NAME = ''

// set artifactory server and maven build
ARTIFACTORY_SERVER = Artifactory.server 'artifactory'
RTMAVEN = Artifactory.newMavenBuild()
RTMAVEN.tool = PATH.findAll(/maven-\d+\.\d+\.\d+/)[0]
RTMAVEN.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: ARTIFACTORY_SERVER
RTMAVEN.resolver releaseRepo: 'swisspost', snapshotRepo: 'swisspost', server: ARTIFACTORY_SERVER
RTMAVEN.deployer.deployArtifacts = false
BUILD_INFO = Artifactory.newBuildInfo()

// checkout git source
public checkoutSource(gitUrl, branchName) {
    checkoutSource(null, gitUrl, branchName)
}

public checkoutSource(projectName, gitUrl, branchName) {
    stage('checkout source') {
        // overwrite the BRANCH_NAME attr with a branch name without `/`
        // because maven can not build with branch names with a `/` in it.
        env.BRANCH_NAME_ORIGINAL = BRANCH_NAME
        BRANCH_NAME = BRANCH_NAME.replaceAll('/', '_')
        LOGGER("checkout", "${gitUrl}")
        git branch: branchName, credentialsId: USER_CREDENTIALS_SCONTINUUM_ID, url: gitUrl
        sh 'git config user.email "s-continuum@post.ch"'
        sh 'git config user.name "s-continuum, IT265"'
    }
}

// build project with maven
public buildMaven(projectName, mavenParams, branchName) {
    buildMaven(projectName, null, mavenParams, branchName, false, null)
}

public buildMaven(projectName, mavenGoals, mavenParams, branchName, useArtifactoryArtifactUploader) {
    buildMaven(projectName, mavenGoals, mavenParams, branchName, useArtifactoryArtifactUploader, null)
}

public buildMaven(projectName, mavenGoals, mavenParams, branchName, useArtifactoryArtifactUploader, additionalBuildNumber) {
    stage('build with maven') {
        RTMAVEN.deployer.deployArtifacts = useArtifactoryArtifactUploader
        if (!mavenParams) {
            mavenParams = ''
        }
        if (!mavenGoals) {
            mavenGoals = 'clean install -U'
        }
        LOGGER("build", "${projectName} mavenParams - ${mavenParams}")
        LOGGER("build", "${projectName} mavenGaols - ${mavenGoals}")
        try {
            // set build number if given - you can set the param with BUILD_NUMBER
            setProjectBranchVersion(projectName, branchName, true, additionalBuildNumber)
            if (MAVEN_POM_NAME.size() > 0) {
                RTMAVEN.run pom: MAVEN_POM_NAME.replace('-f ', ''), goals: mavenGoals + ' ' + mavenParams, buildInfo: BUILD_INFO
            } else {
                RTMAVEN.run pom: 'pom.xml', goals: mavenGoals + ' ' + mavenParams, buildInfo: BUILD_INFO
            }
        } catch (err) {
            saveArtifacts(projectName)
            throw err
        }
        LOGGER("build", "current build status - ${currentBuild.result}")
    }
}

// build Docker image
public buildDockerImage(projectName, imageTag) {
    buildDockerImage(projectName, imageTag, '')    
}

public buildDockerImage(projectName, imageTag, buildArguments) {
    stage('build image') {
        if (!buildArguments) {
            buildArguments = ''
        }
        sh "docker build --pull ${buildArguments} -t \"docker.pnet.ch/${projectName}:${imageTag}\" --no-cache ."
    }
}

// push builded Docker image to artifactory
public pushDockerImage(projectName, imageTag) {
    stage('push image') {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: USER_CREDENTIALS_SJDEPLOY_ID, usernameVariable: 'DOCKERUSER', passwordVariable: 'DOCKERPASS']]) {
            // multiline required because of ( char in the password
            sh '''
                docker login -u $DOCKERUSER -p $DOCKERPASS docker.pnet.ch;
            '''
        }
        sh "docker push docker.pnet.ch/${projectName}:${imageTag}"
        sh "docker rmi docker.pnet.ch/${projectName}:${imageTag}"
    }
}

// run Maven Sonar analysis
public executeSonar(language, additionalParams) {
    stage('SonarQube analysis ' + language) {
        def branch = "Java"
        if (language == "js") {
            branch = "JavaScript"
        }
        withSonarQubeEnv('sonarit') {
            try {
                sh "mvn ${MAVEN_POM_NAME} org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar -Dsonar.scm.disabled=true -Dsonar.branch=${branch} -Dsonar.language=${language} ${additionalParams}"
            } catch (ignore) {
                currentBuild.result = 'UNSTABLE'
            }
        }
    }
}

// start other jenkins job
public startJenkinsJob(jobname, parametersObj, propagate, wait) {
    stage('start jenkins job') {
        build job: jobname, parameters: parametersObj, propagate: propagate, wait: wait
    }
}

// start hostupdater jenkins job
public deployApplication(dockerComposeFile, host, imageProperties) {
    stage('start jenkins job') {
        def jenkinsUrl = "http://jenkinstools.pnet.ch/job/host-updater/buildWithParameters?token=ab4d91358632fd8c45ff05704ed1d9e3&DC_FILE_NAME=${dockerComposeFile}&DC_HOST=${host}&DC_IMAGE_PROPERTIES=${imageProperties}"
        def response = httpRequest httpMode: 'POST', url: jenkinsUrl
        LOGGER("deployment", "jenkins deployment on jenkinstools.pnet.ch successfully started: http://jenkinstools.pnet.ch/job/host-updater")
    }
}


// archive artifacts
public saveArtifacts(projectName) {
    saveArtifacts(projectName, null, null)
}

public saveArtifacts(projectName, buildType, branchName) {
    stage('archive artifacts') {
        publishBuildInformation(buildType, projectName, branchName)
        catchError {
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.yml', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.yaml', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.war', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.jar', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.nar', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.zip', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/serverlogs/**/*.log', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/test-reports/**', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*-test/target/**/*.xml', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*cucumber*.json', fingerprint: false
            archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/**/*.jpg', fingerprint: false
            def cucumberFiles = findFiles glob: "${projectName}-test/target/*cucumber*.json"
            if (cucumberFiles.size() > 0) {
                step([$class: 'CucumberReportPublisher', jsonReportDirectory: "${projectName}-test/target", fileIncludePattern: 'cucumber*.json'])
            }
            junit allowEmptyResults: true, testResults: '**/TEST-*.xml'
        }
    }
}

// send an email if we have an error
public sendEmail(projectName, branchName, jenkinsHost, emailBody, toAllCommiters, toUsers, onlyWhenErrors) {
    if (onlyWhenErrors == false && (currentBuild.result == 'STABLE' || currentBuild.result == 'SUCCESS' || currentBuild.result == 'ABORTED')) {
        sendEmailHelper(projectName, branchName, jenkinsHost, emailBody, toAllCommiters, toUsers)
    }
    if (currentBuild.result == 'UNSTABLE' || currentBuild.result == 'FAILURE') {
        sendEmailHelper(projectName, branchName, jenkinsHost, emailBody, toAllCommiters, toUsers)
    }
}

/** *************************************************************/
/*************** HELPER METHODS FOR JENKINS-PIPELINE ***************/
/** *************************************************************/

// set maven version to a specific version
// if parameter "set" is true, the version would be set, if false unset
def setProjectBranchVersion(projectName, branchName, set) {
    setProjectBranchVersion(projectName, branchName, set, null)
}

def setProjectBranchVersion(projectName, branchName, set, additionalBuildNumber) {
    if (set) {
        PROJECT_VERSION = getServiceVersion(projectName).trim()
        if (branchName == 'develop' || branchName == 'master') {
            LOGGER('version', "if develop or master ${branchName}")
            // set additional build number if given
            if (additionalBuildNumber) {
                PROJECT_BRANCH_VERSION = "${PROJECT_VERSION}-${additionalBuildNumber}"
            } else {
                PROJECT_BRANCH_VERSION = PROJECT_VERSION
            }
        } else {
            LOGGER('version', "if not develop or master ${branchName}")
            def versionWithoutSnapshot = PROJECT_VERSION.replace('-SNAPSHOT', '')
            // set additional build number if given
            if (additionalBuildNumber) {
                PROJECT_BRANCH_VERSION = "${versionWithoutSnapshot}-${branchName}-${additionalBuildNumber}-SNAPSHOT"
            } else {
                PROJECT_BRANCH_VERSION = "${versionWithoutSnapshot}-${branchName}-SNAPSHOT"
            }
            sh "mvn ${MAVEN_POM_NAME} versions:set -DnewVersion=${PROJECT_BRANCH_VERSION}"
        }
        LOGGER("version", "project-version ${PROJECT_VERSION}")
        LOGGER("version", "project-branch-version ${PROJECT_BRANCH_VERSION}")
    } else {
        sh "mvn ${MAVEN_POM_NAME} versions:set -DnewVersion=${PROJECT_VERSION}"
    }
}

// getServiceVersion from pom
def getServiceVersion(project_name) {
    SERVICE_VERSION = sh(
            script: "mvn ${MAVEN_POM_NAME} -q -Dexec.executable=\"echo\" -Dexec.args='\${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec",
            returnStdout: true
    )
    LOGGER("serviceVersion", "${SERVICE_VERSION}")
    return SERVICE_VERSION
}

// set pom filename (-f <name>)
def setPomFileName(filename) {
    MAVEN_POM_NAME = "-f ${filename}"
}

def checkIfDockerTagExists(type, appl, version) {
    def artifactoryUrl = "http://artifactory.pnet.ch/artifactory/api/docker/docker-dev-local/v2/${type}/${appl}/tags/list"
    def response = httpRequest authentication: USER_CREDENTIALS_SJDOCKER_ID, httpMode: 'GET', url: artifactoryUrl, validResponseCodes: '100:499'
    if (response.status == 200) {
        def tagList = parseJson(response.content).tags
        LOGGER("dockertag", "check if the given version ${appl}:${version} matches")
        for (def i = 0; i < tagList.size(); i++) {
            if (tagList[i] == version) {
                LOGGER("dockertag", "given version ${appl}:${version} has matched")
                return true
            }
        }
        LOGGER("dockertag", "given version ${appl}:${version} has not matched any tag!")
        return false
    }
}

// send build information to artifactory
def publishBuildInformation(buildType, projectName, branchName) {
    if (buildType) {
        LOGGER('artifactory', "we got the correct params to deploy build info to artifactory.")
        LOGGER('artifactory', "type: ${buildType}, name: ${projectName}, branch: ${branchName}")
        BUILD_INFO.name = "${projectName}"
        ARTIFACTORY_SERVER.publishBuildInfo(BUILD_INFO)
    } else {
        LOGGER('artifactory', "we din't got the correct params to deploy build info to artifactory.")
    }
}

// send emailsendEmail
def sendEmailHelper(projectName, branchName, jenkinsHost, emailBody, toAllCommiters, toUsers) {
    def url = "http://${jenkinsHost}.pnet.ch/job/${projectName}"
    def buildResult = currentBuild.result
    def recipientProvidersList = [
            [$class: 'CulpritsRecipientProvider'],
            [$class: 'DevelopersRecipientProvider'],
            [$class: 'RequesterRecipientProvider']
    ]
    if (branchName != null) {
        url = url + "/job/${branchName}/${BUILD_NUMBER}"
    }

    if (toAllCommiters == true && recipientProvidersList != null) {
        if (toUsers == null) {
            toUsers = ""
        }
        emailext(
                subject: "${buildResult}: Job ${projectName} - ${branchName}",
                body: """
                <b>${buildResult}: Job ${projectName} - ${branchName}</b><br>
                <p>${emailBody}</p>
                <a href="${url}"><small>${url}</small></a>
                <img href="http://${jenkinsHost}.pnet.ch/userContent/jenkins.png" alt="jenkins" style="position:fixed; top: 20px; right: 20px;">
                """,
                to: toUsers,
                recipientProviders: recipientProvidersList,
        )
    } else if (toAllCommiters == false && toUsers != null) {
        emailext(
                subject: "${buildResult}: Job ${projectName} - ${branchName}",
                body: """
                <b>${buildResult}: Job ${projectName} - ${branchName}</b><br>
                <p>${emailBody}</p>
                <a href="${url}"><small>${url}</small></a>
                <img href="http://${jenkinsHost}.pnet.ch/userContent/jenkins.png" alt="jenkins" style="position:fixed; top: 20px; right: 20px;">
                """,
                to: toUsers
        )
    } else {
        error "no valid email parameter was set, please check documentation"
    }
}

// copy files to host over scp
def scpFileToHost(String hostName, String fileName, String destination){
    sshagent (credentials: ['remotejenkinskey']) {
        sh "scp -o StrictHostKeyChecking=no ${fileName} remotejenkins@${hostName}:${destination}"
    }
}

// execute command on host over ssh
def executeOnHost(String hostName, String command){
    sshagent (credentials: ['remotejenkinskey']) {
        sh "ssh -o StrictHostKeyChecking=no remotejenkins@${hostName} '${command}'"
    }
}

// returns versions as string which can be used in a jenkins choice parameter
def getVersionsFromArtifactory(groupId, artifactId, repo) {
    def url = "http://artifactory.pnet.ch/artifactory/api/search/versions?g=${groupId}&a=${artifactId}&repos=${repo}"
    def response = httpRequest httpMode: 'GET', url: url
    def object = readJSON text: response.content
    def arr = object.results
    def versions = 'RELOAD\n'
    arr.each() {v -> versions = versions + v.version + '\n'}
    return versions
}

// print messages
def LOGGER(title, message) {
    if (!(env.LOGGING && LOGGING == false)) {
        if (title) {
            println "\u276F\u276F\u276F ${title}: ${message}"
        } else {
            println "\u276F\u276F\u276F ${message}"
        }
    }
}

@NonCPS
def parseJson(text) {
    return new groovy.json.JsonSlurperClassic().parseText(text)
}

return this