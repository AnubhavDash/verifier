Build information
=================

The following guide provide step by step informations to build the Verifier Swiss Post on a Windows machine.  

1. Ensure you have Maven and Node installed. We tested with following versions :
    - Maven : 3.3.1
    - Node : v8.3.0-x64
    
2. Using maven, first build the <i>verifier-block3-scytl</i> library by executing a clean&install 
   (repository evoting-verifier-block3-scytl)

3. Install generated verifier-block3-scytl library to your local maven repository :
    <code>mvn install:install-file -Dfile=\<JAR_FILE> -DgroupId=ch.post.it.evoting.verifier -DartifactId=verifier-block3-scytl -Dversion=\<VERSION> -Dpackaging=jar -DgeneratePom=true</code>

4. Create your SSL certificate for REST endpoint
    - <code>openssl req -newkey rsa:2048 -keyout serverkey.pem -x509 -days 365 -out servercertificate.pem -subj /CN=localhost</code>
    - <code>openssl pkcs12 -inkey serverkey.pem -in servercertificate.pem -export -out server-keystore.p12</code>
    - Copy the keystore to verifier-web\src\main\resources
    - Adapt verifier-web\src\main\resources\application.properties with the configuration of your keystore :
        - server.ssl.key-store=classpath:server-keystore.p12
        - server.ssl.key-store-password=YOUR_PASSWORD
        - server.ssl.key-store-type=PKCS12
        - server.ssl.key-alias=1
    - Specify your certificate serial number in verifier-web-client\config.js
        - <code>openssl x509 -in servercertificate.pem -serial -noout</code>
        - <code>serverCertificateSerialNumberToTrust: function() {return 'YOUR_CERTIFICATE_SERIAL_NUMBER';}</code> 

5. Configure node location
    - Edit the file verifier-web-client\bin\_npm.cmd
    - Set the NODE_HOME variable to your node directory location

5. Using Maven, clean&install the Verifier Swiss Post with profile "electron-package"

6. The generated artifact is generated in verifier-assembly\target\verifier-assembly-\<VERSION>.zip

7. Unzip the generated artifact and then launch verifier.exe