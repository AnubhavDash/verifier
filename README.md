Build information
=================

The following guide provide step by step informations to build the Verifier Swiss Post on a Windows machine.  

1. Ensure you have Maven and Node installed. We tested with following versions :
    - Maven : 3.3.1
    - Node : v10.15.3-x64
    
2. First go to the <i>verifier-block3-scytl</i> library (repository evoting-verifier-block3-scytl) and build it using Maven
    - <code>mvn clean install</code>

3. Configure Node location
    - Edit file verifier-web-client\bin\_npm.cmd
    - Set the NODE_HOME variable to your node directory location

4. Create your SSL certificate for REST endpoint
    - <code>openssl req -newkey rsa:2048 -keyout serverkey.pem -x509 -days 365 -out servercertificate.pem -subj /CN=localhost</code>
    - <code>openssl pkcs12 -inkey serverkey.pem -in servercertificate.pem -export -out server-keystore.p12</code>
    - Copy the keystore to verifier-web\src\main\resources
    - Edit verifier-web\src\main\resources\application.properties with the configuration of your keystore :
        - server.ssl.key-store=classpath:YOUR_KEYSTORE  #(mostly: server-keystore.p12)
        - server.ssl.key-store-password=YOUR_PASSWORD
        - server.ssl.key-store-type=PKCS12
        - server.ssl.key-alias=YOUR_ALIAS #(mostly: 1)
    - Specify your certificate serial number in verifier-web-client\config.js
        - <code>openssl x509 -in servercertificate.pem -serial -noout</code>
        - <code>serverCertificateSerialNumberToTrust: function() {return 'YOUR_CERTIFICATE_SERIAL_NUMBER';}</code> 

5. Build using Maven specifying profile "electron-package"
    - <code>mvn clean install -Pelectron-package</code>

6. The generated artifact is generated in verifier-assembly\target\verifier-assembly-\<VERSION>.zip

7. Unzip the generated artifact and then launch verifier.exe