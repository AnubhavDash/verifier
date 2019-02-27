package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineMixing;
import ch.post.it.evoting.verifier.dto.onlinemixing.Signature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test75Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult result = new Test75().executeTest(new File(getClass().getResource("/Test75/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Ignore
    @Test
    public void executeTestNOK() {
        TestResult result = new Test75().executeTest(new File(getClass().getResource("/Test75/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

    @Ignore
    @Test
    public void tddTest75() throws IOException {
        //byte[] platformRootCA = Files.readAllBytes(new File(getClass().getResource("/Test75/OK/certificates/platformRootCA.pem").getFile()).toPath());
        File file = new File(getClass().getResource("/Test75/OK/ballotboxes/dd1b845f19ee422dbb855b6dab93486e/100-03c685d26f7c421d858f8fab7ee7261c-dd1b845f19ee422dbb855b6dab93486e-0-ccn_m1.json").getFile());
        OnlineMixingProofLoader loader = new OnlineMixingProofLoader(file.toPath());

        Signature sig = loader.getOnlineMixing().getSignature();
        List<String> certificateChain = sig.getCertificateChain();
        String certificate = certificateChain.get(0);
        byte[][] intermediates = certificateChain.stream().skip(1).map(str -> str.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new);
        String signature = sig.getSignatureContents();

        //String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        //String contentWithoutSignature = content.replaceAll("(.*)(\"signature\":.*?\"]},)(.*)", "$1$3");

        File fileWithoutSignature = new File(getClass().getResource("/Test75/OK/ballotboxes/dd1b845f19ee422dbb855b6dab93486e/m1-modified.json").getFile());
        String contentWithoutSignature = new String(Files.readAllBytes(fileWithoutSignature.toPath()), StandardCharsets.UTF_8);


        boolean b = SignatureChecker.verifySignature(contentWithoutSignature.getBytes(StandardCharsets.UTF_8),
                TypeConverter.base64ToByte(signature),
                certificate.getBytes(StandardCharsets.UTF_8),
                null,
                null);
        Assert.assertTrue(b);
    }

}