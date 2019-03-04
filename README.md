Build & Packaging informations
==============================

1. Using maven, first build the verifier-block3-scytl library by executing a clean&install (repository evoting-verifier-block3-scytl)

2. Install generated library to your local maven repository :
    <code>
    mvn:install install-file 
        -Dfile=\<JAR_FILE\> 
        -DgroupId=ch.post.it.evoting.verifier                                               
        -DartifactId=verifier-block3-scytl 
        -Dversion=\<VERSION\> 
        -Dpackaging=jar
        -DgeneratePom=true
    </code>

3. Using Maven, clean&install with profile "electron-package"

4. The generated artifact is generated in verifier-assembly\target\verifier-assembly-<VERSION>.zip
5. Unzip the generated artifact and then launch verifier.exe