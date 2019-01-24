package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

public class Test02Test {

    @Test
    @Ignore
    public void executeTestOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Ignore
    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }


    @Test
    @Ignore
    public void testSignature() throws Exception {

        File cert = new File(getClass().getResource("/Test02/OK/certificates/log_sign_keys/cc2_log_sign.pem").getFile());

        /*
        Original from SPLUNK
        {"preview":false,"offset":59,"result":{"_raw":"2019-01-14 09:22:03,717|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::KX306Ie4plliuOVMXSY5veD/M3bnrKYBzfqcsNHkw4c=,ESK::ffAfKu/eCIXP1fHJT1b9+3ezwzMuj8wGy0Rua4AMy62TQokWrYLA0UQlUDA/dyhA6mgJQ4cLMoh+rwyr7Svg7Ckw2STVbNpKBOAOmAJn3xWPiOX8uXn73j0jdqzi6ITKakLOB0OJDYOnWTib1084m/lY2mv0/ZutA41PXJDxWKPpiuzvwWPBNA4GrOZGa3JRGy1C8sfXuVG3LxbvbUH8coxNRXFA6kmMey8ue5qJj+SpIpaCLaNjP+e+vjJ/viTsrUdj09vNPvqKol+HRBX+fHnV1qjbtv3IivaM036LAfe5A+ildl/WsQ0jwkSjP6fWF625ggvWgIccTkto51vl7GgHImMM/wA2JPQ4SfnE+WU5kTYyjWqbwtXKyJJuqgsr/5/J8uhSMtJbGdymdUKdyX3bcP0MmzM/2NW9NpgSHOnhYEhBqVa5ug==,PHMAC::mZIMnPtLjp2j0rts1J9+UtDZRNt6GwCygztb9n5NXmM=,LS::10000,TL::300000,TS::1547454123717,HMAC::9Ygco8ibe2riiJfe6WdQhtq8pNae5uSiKs2OMPFI+uI=,SG::UYECVxcmNH2yuNEBIn+/9F7D1pUMC8sN4yQ3QzdivEupBOixPJqR906OQDuP7eu3IMEIdXcRDIAthe5/Mj4AjzIxqrs0zAtHZd4GMqJ/zwYfDCn5R7CKPGel25ii9QYfZZNq9ofNC+zd0eVfMJq9z1jpIHr2JSxIJPNLo7R3jWDb6vPyzB+kPQb5gUton3fc2ovmT6bUtYWHUaW+QpK4qwlaHmg7ApDm5DFdKPfoJ8vdQVSWtDQaYSbBzI8fyr6CJeLwo00onsSSxgjaAggaUwt6POVa3z7p5KF/YBQFt6tb2AE8Ok0/23QlaQw6ZnevhoDNTfHiZkPpU7fC/3IAXw==*}","_time":"2019-01-14T09:22:03.717+0100","host":"h002gp","index":"it_evoting_cc","linecount":"1","source":"D:\\logs_3\\cv\\logs\\cv_secure-20190114-083203-4.log","sourcetype":"post_evoting_securelogs","splunk_server":"hin03a.pnet.ch"}}

        Original from FILE
        2019-01-14 09:22:03,717|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::KX306Ie4plliuOVMXSY5veD/M3bnrKYBzfqcsNHkw4c=,ESK::ffAfKu/eCIXP1fHJT1b9+3ezwzMuj8wGy0Rua4AMy62TQokWrYLA0UQlUDA/dyhA6mgJQ4cLMoh+rwyr7Svg7Ckw2STVbNpKBOAOmAJn3xWPiOX8uXn73j0jdqzi6ITKakLOB0OJDYOnWTib1084m/lY2mv0/ZutA41PXJDxWKPpiuzvwWPBNA4GrOZGa3JRGy1C8sfXuVG3LxbvbUH8coxNRXFA6kmMey8ue5qJj+SpIpaCLaNjP+e+vjJ/viTsrUdj09vNPvqKol+HRBX+fHnV1qjbtv3IivaM036LAfe5A+ildl/WsQ0jwkSjP6fWF625ggvWgIccTkto51vl7GgHImMM/wA2JPQ4SfnE+WU5kTYyjWqbwtXKyJJuqgsr/5/J8uhSMtJbGdymdUKdyX3bcP0MmzM/2NW9NpgSHOnhYEhBqVa5ug==,PHMAC::mZIMnPtLjp2j0rts1J9+UtDZRNt6GwCygztb9n5NXmM=,LS::10000,TL::300000,TS::1547454123717,HMAC::9Ygco8ibe2riiJfe6WdQhtq8pNae5uSiKs2OMPFI+uI=,SG::UYECVxcmNH2yuNEBIn+/9F7D1pUMC8sN4yQ3QzdivEupBOixPJqR906OQDuP7eu3IMEIdXcRDIAthe5/Mj4AjzIxqrs0zAtHZd4GMqJ/zwYfDCn5R7CKPGel25ii9QYfZZNq9ofNC+zd0eVfMJq9z1jpIHr2JSxIJPNLo7R3jWDb6vPyzB+kPQb5gUton3fc2ovmT6bUtYWHUaW+QpK4qwlaHmg7ApDm5DFdKPfoJ8vdQVSWtDQaYSbBzI8fyr6CJeLwo00onsSSxgjaAggaUwt6POVa3z7p5KF/YBQFt6tb2AE8Ok0/23QlaQw6ZnevhoDNTfHiZkPpU7fC/3IAXw==*}
    */


        String sg = "UYECVxcmNH2yuNEBIn+/9F7D1pUMC8sN4yQ3QzdivEupBOixPJqR906OQDuP7eu3IMEIdXcRDIAthe5/Mj4AjzIxqrs0zAtHZd4GMqJ/zwYfDCn5R7CKPGel25ii9QYfZZNq9ofNC+zd0eVfMJq9z1jpIHr2JSxIJPNLo7R3jWDb6vPyzB+kPQb5gUton3fc2ovmT6bUtYWHUaW+QpK4qwlaHmg7ApDm5DFdKPfoJ8vdQVSWtDQaYSbBzI8fyr6CJeLwo00onsSSxgjaAggaUwt6POVa3z7p5KF/YBQFt6tb2AE8Ok0/23QlaQw6ZnevhoDNTfHiZkPpU7fC/3IAXw==";

        String value = "2019-01-14 09:22:03,717|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::KX306Ie4plliuOVMXSY5veD/M3bnrKYBzfqcsNHkw4c=,ESK::ffAfKu/eCIXP1fHJT1b9+3ezwzMuj8wGy0Rua4AMy62TQokWrYLA0UQlUDA/dyhA6mgJQ4cLMoh+rwyr7Svg7Ckw2STVbNpKBOAOmAJn3xWPiOX8uXn73j0jdqzi6ITKakLOB0OJDYOnWTib1084m/lY2mv0/ZutA41PXJDxWKPpiuzvwWPBNA4GrOZGa3JRGy1C8sfXuVG3LxbvbUH8coxNRXFA6kmMey8ue5qJj+SpIpaCLaNjP+e+vjJ/viTsrUdj09vNPvqKol+HRBX+fHnV1qjbtv3IivaM036LAfe5A+ildl/WsQ0jwkSjP6fWF625ggvWgIccTkto51vl7GgHImMM/wA2JPQ4SfnE+WU5kTYyjWqbwtXKyJJuqgsr/5/J8uhSMtJbGdymdUKdyX3bcP0MmzM/2NW9NpgSHOnhYEhBqVa5ug==,PHMAC::mZIMnPtLjp2j0rts1J9+UtDZRNt6GwCygztb9n5NXmM=,LS::10000,TL::300000,TS::1547454123717,HMAC::9Ygco8ibe2riiJfe6WdQhtq8pNae5uSiKs2OMPFI+uI=*}\n";

        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        final X509Certificate sCert = loadCertificate(Files.readAllBytes(cert.toPath()));
        final String algoName = "SHA256withRSAandMGF1";

        Signature signatureAlgorithm = Signature.getInstance(algoName);
        signatureAlgorithm.setParameter(new PSSParameterSpec("SHA-256", "MGF1",
                new MGF1ParameterSpec("SHA-256"), 32, 1));

        signatureAlgorithm.initVerify(sCert.getPublicKey());
        signatureAlgorithm.update(value.getBytes(StandardCharsets.UTF_8));

        Assert.assertTrue(signatureAlgorithm.verify(Base64.decode(sg)));
    }

    private X509Certificate loadCertificate(byte[] certificate) throws IOException, CertificateException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        PEMParser parser = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parser.readObject());
    }

}