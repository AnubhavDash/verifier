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

        File cert = new File(getClass().getResource("/Test02/OK/certificates/log_sign_keys/cc3_log_sign.pem").getFile());

        /*
        Original from SPLUNK
        {"preview":false,"offset":11,"result":{"_raw":"2019-01-10 17:44:56,022|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::Xt85x+juIaKxIJH/C1X89jaxo8hHCMTpchh/CmWEOG4=,ESK::Yfoid5mrIqy5FudEtUvs/3XEzXe33DTwr6MDC/KbnNaKFHNW7NdC6TJMrrWseQGmSHAdQa4jLdiVnmToGoXmZLHq3dLt3R2oGiq84MxE2QDkUT5sjP1vfmT2X3P26cdtpUzL3f9QqHGx0pyDRZV1GmaAF7nP+ZH+9zQ99Pnq+crF2y6PXuffdaZHjo/2N0PsW4H3SQOoJRuav2bKZ5xtLVj/Wi52DImXyqrI3pV4iBmyBUUqgzyEChmVVMOgu3fO3nvzv8rlh68JG7EPUxbxw41fece9Tm5qlL27C+wfSBQRj93lZOI1pyAN3no5N9zjDcNXr9tNbvF+UcG0ejkG3tFR4M+ZsCPQvJ05wg/6zICexlOYBCUG7mEweC+PDIlrllheD1d4CwutmttOmfQzxhITmo1nZGE3vvjCzGTOk9fJiE8922Ld5w==,PHMAC::w2rbwgXcVLsG0qfKeMkMie71zK1Yj/qJsgIZBMMLBEo=,LS::10000,TL::300000,TS::1547138696023,HMAC::o1NK3+r4orcE1RD80fuoQZqfBoV1tWpN6Vf1ZR//B0I=,SG::SP58ptjggnKtPo3hIyPmclHIgdOsuOiNbnHUQSXRwiMnNSrialyAy/D6jYRcV7N/x+pMSSszhkujkknVBPBsnRmCfGTaW2q+Q6FlOjG7Ubf2eQi5HWhL9mcaHmANJYYSKO3JeZLWEuz5JEcXFBiKpmsptCVgAJvWAPWRAB1Yx6EoJyk17jJIj8QRB6R3TUc2xWVtdIEHYT7O1UcF6UbD6u4ppjfurNd+kD3NmwtJli5FWr7UCxBUUlGZRdebeDIoH4hENjPZ7wHynbMVK/CyIWbwVBEN6nWP24kQ9P5BbzE3Jd8WYG+DeYM3XTUGEyj29YOs4SlYCHtRUCOXdtw0BQ==*}","_time":"2019-01-10T17:44:56.022+0100","host":"h002gf","index":"it_evoting_cc","linecount":"1","source":"/data/logs/cv/logs/cv_secure-20190110-161956-60.log","sourcetype":"post_evoting_securelogs","splunk_server":"hlog8a.pnet.ch"}}

        Original from FILE
        2019-01-10 17:44:56,022|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::Xt85x+juIaKxIJH/C1X89jaxo8hHCMTpchh/CmWEOG4=,ESK::Yfoid5mrIqy5FudEtUvs/3XEzXe33DTwr6MDC/KbnNaKFHNW7NdC6TJMrrWseQGmSHAdQa4jLdiVnmToGoXmZLHq3dLt3R2oGiq84MxE2QDkUT5sjP1vfmT2X3P26cdtpUzL3f9QqHGx0pyDRZV1GmaAF7nP+ZH+9zQ99Pnq+crF2y6PXuffdaZHjo/2N0PsW4H3SQOoJRuav2bKZ5xtLVj/Wi52DImXyqrI3pV4iBmyBUUqgzyEChmVVMOgu3fO3nvzv8rlh68JG7EPUxbxw41fece9Tm5qlL27C+wfSBQRj93lZOI1pyAN3no5N9zjDcNXr9tNbvF+UcG0ejkG3tFR4M+ZsCPQvJ05wg/6zICexlOYBCUG7mEweC+PDIlrllheD1d4CwutmttOmfQzxhITmo1nZGE3vvjCzGTOk9fJiE8922Ld5w==,PHMAC::w2rbwgXcVLsG0qfKeMkMie71zK1Yj/qJsgIZBMMLBEo=,LS::10000,TL::300000,TS::1547138696023,HMAC::o1NK3+r4orcE1RD80fuoQZqfBoV1tWpN6Vf1ZR//B0I=,SG::SP58ptjggnKtPo3hIyPmclHIgdOsuOiNbnHUQSXRwiMnNSrialyAy/D6jYRcV7N/x+pMSSszhkujkknVBPBsnRmCfGTaW2q+Q6FlOjG7Ubf2eQi5HWhL9mcaHmANJYYSKO3JeZLWEuz5JEcXFBiKpmsptCVgAJvWAPWRAB1Yx6EoJyk17jJIj8QRB6R3TUc2xWVtdIEHYT7O1UcF6UbD6u4ppjfurNd+kD3NmwtJli5FWr7UCxBUUlGZRdebeDIoH4hENjPZ7wHynbMVK/CyIWbwVBEN6nWP24kQ9P5BbzE3Jd8WYG+DeYM3XTUGEyj29YOs4SlYCHtRUCOXdtw0BQ==*}
    */


        String sg = "SP58ptjggnKtPo3hIyPmclHIgdOsuOiNbnHUQSXRwiMnNSrialyAy/D6jYRcV7N/x+pMSSszhkujkknVBPBsnRmCfGTaW2q+Q6FlOjG7Ubf2eQi5HWhL9mcaHmANJYYSKO3JeZLWEuz5JEcXFBiKpmsptCVgAJvWAPWRAB1Yx6EoJyk17jJIj8QRB6R3TUc2xWVtdIEHYT7O1UcF6UbD6u4ppjfurNd+kD3NmwtJli5FWr7UCxBUUlGZRdebeDIoH4hENjPZ7wHynbMVK/CyIWbwVBEN6nWP24kQ9P5BbzE3Jd8WYG+DeYM3XTUGEyj29YOs4SlYCHtRUCOXdtw0BQ==";

        String value = "2019-01-10 17:44:56,022|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::Xt85x+juIaKxIJH/C1X89jaxo8hHCMTpchh/CmWEOG4=,ESK::Yfoid5mrIqy5FudEtUvs/3XEzXe33DTwr6MDC/KbnNaKFHNW7NdC6TJMrrWseQGmSHAdQa4jLdiVnmToGoXmZLHq3dLt3R2oGiq84MxE2QDkUT5sjP1vfmT2X3P26cdtpUzL3f9QqHGx0pyDRZV1GmaAF7nP+ZH+9zQ99Pnq+crF2y6PXuffdaZHjo/2N0PsW4H3SQOoJRuav2bKZ5xtLVj/Wi52DImXyqrI3pV4iBmyBUUqgzyEChmVVMOgu3fO3nvzv8rlh68JG7EPUxbxw41fece9Tm5qlL27C+wfSBQRj93lZOI1pyAN3no5N9zjDcNXr9tNbvF+UcG0ejkG3tFR4M+ZsCPQvJ05wg/6zICexlOYBCUG7mEweC+PDIlrllheD1d4CwutmttOmfQzxhITmo1nZGE3vvjCzGTOk9fJiE8922Ld5w==,PHMAC::w2rbwgXcVLsG0qfKeMkMie71zK1Yj/qJsgIZBMMLBEo=,LS::10000,TL::300000,TS::1547138696023,HMAC::o1NK3+r4orcE1RD80fuoQZqfBoV1tWpN6Vf1ZR//B0I=,SG::SP58ptjggnKtPo3hIyPmclHIgdOsuOiNbnHUQSXRwiMnNSrialyAy/D6jYRcV7N/x+pMSSszhkujkknVBPBsnRmCfGTaW2q+Q6FlOjG7Ubf2eQi5HWhL9mcaHmANJYYSKO3JeZLWEuz5JEcXFBiKpmsptCVgAJvWAPWRAB1Yx6EoJyk17jJIj8QRB6R3TUc2xWVtdIEHYT7O1UcF6UbD6u4ppjfurNd+kD3NmwtJli5FWr7UCxBUUlGZRdebeDIoH4hENjPZ7wHynbMVK/CyIWbwVBEN6nWP24kQ9P5BbzE3Jd8WYG+DeYM3XTUGEyj29YOs4SlYCHtRUCOXdtw0BQ==*}\n";

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