/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.securelog.CheckPointLogEntry;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundle;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCertificates;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogMetadata;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Test02Test {

    @Test
    @Ignore
    public void executeTestOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }


    @Test
    public void testSignatureAlgorithm() throws Exception {

        File cert = new File(getClass().getResource("/Test02/testSignature/cc1_log_sign.pem").getFile());
        File intermediate = new File(getClass().getResource("/Test02/testSignature/cc1_ca.pem").getFile());
        File root = new File(getClass().getResource("/Test02/testSignature/platformRootCA.pem").getFile());

        /*
        Original from SPLUNK
        {"preview":false,"offset":2204,"result":{"_raw":"2019-01-29 00:02:38,204|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::EZHSddXYfgf5bACUt4R9f1XuIU+bjwewPNrVGWq3crs=,ESK::g9PB+b3UVRxBv/d349PO2VdOVD4JJMGljtobS+CD2sAW/1fnKqOYqzjBJPB1ZE/nTrzxFUnmO+31yTHY3ORWNZsG7DcmtZt4RT4ckqvuEzx8TOb26mhZ+YJ8Lc5Ln+14tXZ5uZBpUFVXprw5rwZ48PNj8UaC9O4nw1Tmi0LGnI2bRr7bLLvEfKJACRitNLf5uVUTnvsJcf7iMU0mWJsApptBz2z9IxiA/+alX8jwk1RCIaFKTsxSEwpmim52aDjYZSEJgyo9wfdtmHofZdXnX7Aq3BEfl2S8iuEYFes6xKOAzxENEGoNfUeD5YJxNIj9IGUBwrck0Ys17i5pUiNAxheZwl9/HyE0B9/ApGcMhmC4tSU/EQ8R7cItnJ1llnwPdhG+647klQuZrjZq9C75ak/YtPbDY62k5WSHZThg/4k7xW3b3SS0mg==,PHMAC::LU9MozP14HFR7ggG7NSesx2Ztksa3PmPNiuR9qK9DF4=,LS::10000,TL::300000,TS::1548716558205,HMAC::XxzCeo2Zb2sgvZY8fUM/hHBGYkx4Oo8xdsUvT5O0cZA=,SG::pUFV4DWoimWtePDunO5VapLy0YtBkDFYF5f1UhXftYRxOx5ID7XmU/m2R12w0YNFY9+boelq9wSMC6ARbVkFMJ5I9Nud3gjIvIuT+LKZrHMoPkGiM9mWqiO0v19YpNz3HfD2GKLrg3ZS+L/NW2jRUSgywqPBFcAkcmu7TY5p9flVDBR2y6Bu33dpmzZ0g/ERbnDrBUH4UxqdPNw9noIdGKjbQjQZu5/CnybUR4sO2M4sXO4F1Ces0h7hUBw9n81WgOgXK78YNusR8inoviwq3v5kVMrwX/DsThgYdcJSiLCeJ03Yls6ssBI0fM2cZIeRbdBL3G1ANRrsPK1ZtPn6Hg==*}","_time":"2019-01-29T00:02:38.204+0100","arrivalcode":"6592061","cd":"68:6592061","host":"h002gb","index":"it_evoting_cc","linecount":"1","minute":"05290002","source":"/data/logs/cv/logs/cv_secure-20190125-113237-106.log","sourcetype":"post_evoting_securelogs","splunk_server":"hin01a.pnet.ch"}}

        Original from FILE
        2019-01-29 00:02:38,204|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::EZHSddXYfgf5bACUt4R9f1XuIU+bjwewPNrVGWq3crs=,ESK::g9PB+b3UVRxBv/d349PO2VdOVD4JJMGljtobS+CD2sAW/1fnKqOYqzjBJPB1ZE/nTrzxFUnmO+31yTHY3ORWNZsG7DcmtZt4RT4ckqvuEzx8TOb26mhZ+YJ8Lc5Ln+14tXZ5uZBpUFVXprw5rwZ48PNj8UaC9O4nw1Tmi0LGnI2bRr7bLLvEfKJACRitNLf5uVUTnvsJcf7iMU0mWJsApptBz2z9IxiA/+alX8jwk1RCIaFKTsxSEwpmim52aDjYZSEJgyo9wfdtmHofZdXnX7Aq3BEfl2S8iuEYFes6xKOAzxENEGoNfUeD5YJxNIj9IGUBwrck0Ys17i5pUiNAxheZwl9/HyE0B9/ApGcMhmC4tSU/EQ8R7cItnJ1llnwPdhG+647klQuZrjZq9C75ak/YtPbDY62k5WSHZThg/4k7xW3b3SS0mg==,PHMAC::LU9MozP14HFR7ggG7NSesx2Ztksa3PmPNiuR9qK9DF4=,LS::10000,TL::300000,TS::1548716558205,HMAC::XxzCeo2Zb2sgvZY8fUM/hHBGYkx4Oo8xdsUvT5O0cZA=*}

        SG::pUFV4DWoimWtePDunO5VapLy0YtBkDFYF5f1UhXftYRxOx5ID7XmU/m2R12w0YNFY9+boelq9wSMC6ARbVkFMJ5I9Nud3gjIvIuT+LKZrHMoPkGiM9mWqiO0v19YpNz3HfD2GKLrg3ZS+L/NW2jRUSgywqPBFcAkcmu7TY5p9flVDBR2y6Bu33dpmzZ0g/ERbnDrBUH4UxqdPNw9noIdGKjbQjQZu5/CnybUR4sO2M4sXO4F1Ces0h7hUBw9n81WgOgXK78YNusR8inoviwq3v5kVMrwX/DsThgYdcJSiLCeJ03Yls6ssBI0fM2cZIeRbdBL3G1ANRrsPK1ZtPn6Hg==
    */


        String sg = "pUFV4DWoimWtePDunO5VapLy0YtBkDFYF5f1UhXftYRxOx5ID7XmU/m2R12w0YNFY9+boelq9wSMC6ARbVkFMJ5I9Nud3gjIvIuT+LKZrHMoPkGiM9mWqiO0v19YpNz3HfD2GKLrg3ZS+L/NW2jRUSgywqPBFcAkcmu7TY5p9flVDBR2y6Bu33dpmzZ0g/ERbnDrBUH4UxqdPNw9noIdGKjbQjQZu5/CnybUR4sO2M4sXO4F1Ces0h7hUBw9n81WgOgXK78YNusR8inoviwq3v5kVMrwX/DsThgYdcJSiLCeJ03Yls6ssBI0fM2cZIeRbdBL3G1ANRrsPK1ZtPn6Hg==";

        String value = "2019-01-29 00:02:38,204|DEBUG|TIMER-LOG|New Secret Key generated. {*LSK::EZHSddXYfgf5bACUt4R9f1XuIU+bjwewPNrVGWq3crs=,ESK::g9PB+b3UVRxBv/d349PO2VdOVD4JJMGljtobS+CD2sAW/1fnKqOYqzjBJPB1ZE/nTrzxFUnmO+31yTHY3ORWNZsG7DcmtZt4RT4ckqvuEzx8TOb26mhZ+YJ8Lc5Ln+14tXZ5uZBpUFVXprw5rwZ48PNj8UaC9O4nw1Tmi0LGnI2bRr7bLLvEfKJACRitNLf5uVUTnvsJcf7iMU0mWJsApptBz2z9IxiA/+alX8jwk1RCIaFKTsxSEwpmim52aDjYZSEJgyo9wfdtmHofZdXnX7Aq3BEfl2S8iuEYFes6xKOAzxENEGoNfUeD5YJxNIj9IGUBwrck0Ys17i5pUiNAxheZwl9/HyE0B9/ApGcMhmC4tSU/EQ8R7cItnJ1llnwPdhG+647klQuZrjZq9C75ak/YtPbDY62k5WSHZThg/4k7xW3b3SS0mg==,PHMAC::LU9MozP14HFR7ggG7NSesx2Ztksa3PmPNiuR9qK9DF4=,LS::10000,TL::300000,TS::1548716558205,HMAC::XxzCeo2Zb2sgvZY8fUM/hHBGYkx4Oo8xdsUvT5O0cZA=*}\n";

        boolean b = SignatureChecker.verifySignature(value.getBytes(StandardCharsets.UTF_8), Base64.decode(sg), Files.readAllBytes(cert.toPath()), new byte[][]{Files.readAllBytes(intermediate.toPath())}, Files.readAllBytes(root.toPath()));

        Assert.assertTrue(b);
    }

    @Test
    public void testSecureLogBundleSignature() throws Exception {
        File cert = new File(getClass().getResource("/Test02/testSignature/cc1_log_sign.pem").getFile());
        File intermediate = new File(getClass().getResource("/Test02/testSignature/cc1_ca.pem").getFile());
        File root = new File(getClass().getResource("/Test02/testSignature/platformRootCA.pem").getFile());

        SecureLogBundle bundle = new SecureLogBundle();
        CheckPointLogEntry checkpoint = new CheckPointLogEntry();
        SecureLogMetadata metadata = new SecureLogMetadata();
        SecureLogBundleCertificates certificates = new SecureLogBundleCertificates();

        metadata.setLsk("EZHSddXYfgf5bACUt4R9f1XuIU+bjwewPNrVGWq3crs=");
        metadata.setEsk("g9PB+b3UVRxBv/d349PO2VdOVD4JJMGljtobS+CD2sAW/1fnKqOYqzjBJPB1ZE/nTrzxFUnmO+31yTHY3ORWNZsG7DcmtZt4RT4ckqvuEzx8TOb26mhZ+YJ8Lc5Ln+14tXZ5uZBpUFVXprw5rwZ48PNj8UaC9O4nw1Tmi0LGnI2bRr7bLLvEfKJACRitNLf5uVUTnvsJcf7iMU0mWJsApptBz2z9IxiA/+alX8jwk1RCIaFKTsxSEwpmim52aDjYZSEJgyo9wfdtmHofZdXnX7Aq3BEfl2S8iuEYFes6xKOAzxENEGoNfUeD5YJxNIj9IGUBwrck0Ys17i5pUiNAxheZwl9/HyE0B9/ApGcMhmC4tSU/EQ8R7cItnJ1llnwPdhG+647klQuZrjZq9C75ak/YtPbDY62k5WSHZThg/4k7xW3b3SS0mg==");
        metadata.setPhmac("LU9MozP14HFR7ggG7NSesx2Ztksa3PmPNiuR9qK9DF4=");
        metadata.setLs("10000");
        metadata.setTl("300000");
        metadata.setTs("1548716558205");
        metadata.setHmac("XxzCeo2Zb2sgvZY8fUM/hHBGYkx4Oo8xdsUvT5O0cZA=");
        metadata.setSg("pUFV4DWoimWtePDunO5VapLy0YtBkDFYF5f1UhXftYRxOx5ID7XmU/m2R12w0YNFY9+boelq9wSMC6ARbVkFMJ5I9Nud3gjIvIuT+LKZrHMoPkGiM9mWqiO0v19YpNz3HfD2GKLrg3ZS+L/NW2jRUSgywqPBFcAkcmu7TY5p9flVDBR2y6Bu33dpmzZ0g/ERbnDrBUH4UxqdPNw9noIdGKjbQjQZu5/CnybUR4sO2M4sXO4F1Ces0h7hUBw9n81WgOgXK78YNusR8inoviwq3v5kVMrwX/DsThgYdcJSiLCeJ03Yls6ssBI0fM2cZIeRbdBL3G1ANRrsPK1ZtPn6Hg==");

        checkpoint.setRaw("2019-01-29 00:02:38,204|DEBUG|TIMER-LOG|New Secret Key generated.\n");
        checkpoint.setMetadata(metadata);

        certificates.setCertificate(Files.readAllBytes(cert.toPath()));
        certificates.setIntermediate(Files.readAllBytes(intermediate.toPath()));
        certificates.setRoot(Files.readAllBytes(root.toPath()));

        bundle.setCertificates(certificates);
        bundle.setBeginCheckPoint(checkpoint);
        bundle.setEndCheckPoint(checkpoint);

        bundle.validateSignature();
    }
}