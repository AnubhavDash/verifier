Build information
=================

The following guide provide step by step informations to build the Verifier Swiss Post on a Windows machine.  

1. Ensure you have Maven and Node installed. We tested with following versions:
    - AdoptOpenJDK: 11.0.9.1+1
    - Maven: 3.6.3
    - Node: v14.15.1
    
2. First go to the <i>verifier-block3-scytl</i> library (repository evoting-verifier-block3-scytl) and build it using Maven
    - <code>mvn clean install</code>

3. Configure Node location
    - Edit file verifier-web-client\bin\_npm.cmd
    - Set the NODE_HOME variable to your node directory location

4. Build using Maven specifying profile "electron-package"
    - <code>mvn clean install -Pelectron-package</code>

5. The generated artifact is generated in verifier-assembly\target\verifier-assembly-\<VERSION>.zip

6. Unzip the generated artifact and then launch verifier.exe
