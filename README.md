Build information
=================

The following guide provide step by step informations to build the Verifier Swiss Post on a Windows machine.  

1. Ensure you have Maven and Node installed. We tested with following versions :
    - Maven : 3.3.1
    - Node : v8.3.0-x64
    
2. Using maven, first build the <i>verifier-block3-scytl</i> library by executing a clean&install 
   (repository evoting-verifier-block3-scytl)

3. Install generated library to your local maven repository :
    <code>
    mvn:install install-file 
        -Dfile=\<JAR_FILE> 
        -DgroupId=ch.post.it.evoting.verifier                                               
        -DartifactId=verifier-block3-scytl 
        -Dversion=\<VERSION> 
        -Dpackaging=jar
        -DgeneratePom=true
    </code>

4. Using Maven, clean&install with profile "electron-package"

5. The generated artifact is generated in verifier-assembly\target\verifier-assembly-<VERSION>.zip

6. Unzip the generated artifact and then launch verifier.exe