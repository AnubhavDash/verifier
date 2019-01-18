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

public class Test02Test {

    @Test
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

    @Ignore
    @Test
    public void testSignature() throws Exception {

    /*
        2019-01-10 00:01:46,095|DEBUG|TIMER-LOG|New Secret Key generated.
        {*
       LSK::OneANGpMCDuGbDBjdRp+VTFsvpP9cVOqcOzQQZvI6hM=
        ESK::OSXWkIYHyK6GdeBMoA9R2i3Y7MOL591WFTy4bppilfd2uMPXoRD27ziSQ4U1nQ9QjB1yrw1c2SdYoxUMocBdYEfAUaanmB2+avS2Dgf+mpTkEAmSIwtSBPCcXACOVKUxS3ygTqJt5QEQK6a0py85yWiuq6bTtMdkI8VWoayc6//XZasZKOB1LhoHeQGb18JMJmxVN4F3zhnz68OHRlPi3zKY4PBvR4Wq6Q747hVsF6fk4C5dKoC3ZXfQpge5P2ciYosEcCVWKfecAVs4xuA2dDTuNVmG6xz0DU69ST0QWnWq01bjGloZDuR/TY4p/U2p+wpstL61NarOOvp71BjtkeB+SQ6yXF+WMeTeGzqMi1XlSNIBDa1tL9HYHX/oTy+Syd5EqVfZ8CKCufnAMfrkRNwY2U5giAunSnurx+NjhVeLypII1OCm6A==
       PHMAC::/U7KWuSpH7HMtdGmH5CBzTQy1ig/azEtZooVVOyb6qo=
        LS::10000
        TL::300000
        TS::1547074906095
        HMAC::2Inz6oEU/aP6mxCu+wh5t/GWHla5yS8tnPfytbr0qP0=
        SG::myo/yPXhCz0BnX7009/kKvUfrqlv+sJav0uXdVW5gUBoT6AIGJPphUI7JUg4YScCCztwSiYGvTGG5BSALNRpKqyAK1FkCfoFFxMwCx8KcRlBqFI9UEBiVL6RWCKfJV6rakgBV4MIEIq/mXyd3hs/GIx9jFg8jG/uQp/sueMdu98bf+EwZRtKFLB85uuail7eJAVQkl97PXfHdtb2TsrXUkbAPF4G9SWdzzNqCt21OcjqSME7J28WpK6KHUVhii8JPKOc8AKzWe335xvd1MV5wP3k1ytSaL67eDBp/bCjdUiHgsAGC5aefDwXJjWvWYwKJ7FL3JV3/UvSZ5RN+IbEwA==
        *}
    */

        String raw = "2019-01-10 00:01:46,095|DEBUG|TIMER-LOG|New Secret Key generated.";
        String hmac = "2Inz6oEU/aP6mxCu+wh5t/GWHla5yS8tnPfytbr0qP0=";
        String phmac = "/U7KWuSpH7HMtdGmH5CBzTQy1ig/azEtZooVVOyb6qo=";
        String lsk = "OneANGpMCDuGbDBjdRp+VTFsvpP9cVOqcOzQQZvI6hM=";
        String esk = "OSXWkIYHyK6GdeBMoA9R2i3Y7MOL591WFTy4bppilfd2uMPXoRD27ziSQ4U1nQ9QjB1yrw1c2SdYoxUMocBdYEfAUaanmB2+avS2Dgf+mpTkEAmSIwtSBPCcXACOVKUxS3ygTqJt5QEQK6a0py85yWiuq6bTtMdkI8VWoayc6//XZasZKOB1LhoHeQGb18JMJmxVN4F3zhnz68OHRlPi3zKY4PBvR4Wq6Q747hVsF6fk4C5dKoC3ZXfQpge5P2ciYosEcCVWKfecAVs4xuA2dDTuNVmG6xz0DU69ST0QWnWq01bjGloZDuR/TY4p/U2p+wpstL61NarOOvp71BjtkeB+SQ6yXF+WMeTeGzqMi1XlSNIBDa1tL9HYHX/oTy+Syd5EqVfZ8CKCufnAMfrkRNwY2U5giAunSnurx+NjhVeLypII1OCm6A==";
        String ls = "10000";
        String tl = "300000";
        String ts = "1547074906095";
        String sg = "myo/yPXhCz0BnX7009/kKvUfrqlv+sJav0uXdVW5gUBoT6AIGJPphUI7JUg4YScCCztwSiYGvTGG5BSALNRpKqyAK1FkCfoFFxMwCx8KcRlBqFI9UEBiVL6RWCKfJV6rakgBV4MIEIq/mXyd3hs/GIx9jFg8jG/uQp/sueMdu98bf+EwZRtKFLB85uuail7eJAVQkl97PXfHdtb2TsrXUkbAPF4G9SWdzzNqCt21OcjqSME7J28WpK6KHUVhii8JPKOc8AKzWe335xvd1MV5wP3k1ytSaL67eDBp/bCjdUiHgsAGC5aefDwXJjWvWYwKJ7FL3JV3/UvSZ5RN+IbEwA==";

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            try (DataOutputStream stream = new DataOutputStream(bytes)) {
                stream.write(Base64.decode(hmac));
                stream.write(Base64.decode(phmac));
                stream.write(Base64.decode(esk));
                stream.write(Base64.decode(lsk));
                stream.writeInt(Integer.parseInt(ls));
                stream.writeLong(Long.parseLong(tl));
                stream.writeLong(Long.parseLong(ts));
                stream.write(raw.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException("Unable to serialize secureLogEntry", e);
            }

            File cert = new File(getClass().getResource("/Test02/OK/certificates/log_sign_keys/cc3_log_sign.pem").getFile());


            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            final X509Certificate sCert = loadCertificate(Files.readAllBytes(cert.toPath()));
            final String algoName = "SHA256withRSAandMGF1";

            Signature signatureAlgorithm = Signature.getInstance(algoName);
            signatureAlgorithm.initVerify(sCert.getPublicKey());
            signatureAlgorithm.update(bytes.toByteArray());

            Assert.assertTrue(signatureAlgorithm.verify(Base64.decode(sg)));
        }
    }

    private X509Certificate loadCertificate(byte[] certificate) throws IOException, CertificateException {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        PEMParser parser = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parser.readObject());
    }


}