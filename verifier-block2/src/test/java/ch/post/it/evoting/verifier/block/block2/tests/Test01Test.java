package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test01Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Ignore
    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void testReduceToBundle() throws IOException {
        Path path = new File(getClass().getResource("/Test01/OK/Evoting_CC_verifier_export_7d-json.json").getFile()).toPath();
        Stream<SecureLogEntry> logEntryStream = Files.lines(path).map(line -> {
            try {
                return SecureLogEntry.from(line);
            } catch (IOException e) {
                //TODO handle exception if deserialize failed
                e.printStackTrace();
                return null;
            }
        });

        /*Stream<SecureLogBundle> secureLogBundleStream = SecureLogBundleCreator.from(logEntryStream);
        Assert.assertEquals(59717, secureLogBundleStream.count());*/
        /*secureLogBundleStream.forEach(b -> {
            try {
                b.validateIntegrity();
            } catch (SecureLogBundleValidationException e) {
                e.printStackTrace();
            }
        });*/
    }

    @Test
    @Ignore
    public void reverseFile() throws IOException {
        Path path = new File(getClass().getResource("/Test01/OK/Evoting_CC_verifier_export_7d-json.json").getFile()).toPath();
        Stream<String> stream = Files.lines(path);
        List<String> collect = stream.collect(Collectors.toList());
        File file = new File("c:\\temp\\result.json");
        FileWriter fw = new FileWriter("c:\\temp\\result.json");
        for (int i = collect.size() - 1; i >= 0; i--) {
            fw.write(collect.get(i) + "\r\n");
        }
        fw.close();
    }

    @Ignore
    @Test
    public void generateHmac() {
        final String endLsk = "U9LwEJ1oDitWij/tX8SJ44FqDGrFFhJXXr+Nakj509w=";
        final String hmac = "cxEQsXjOFU09oL8gGvDMSC0TzYDwtGrJdfL27iOzNnQ=";
        final String phmac = "qwQjgCt3g7+6MPQen2pB4weT1e/FgBvl2iRn/LOJ3IU=";
        final String lsk = "Jd10KtoV11/a3XgJwIAU71K0PCjDBTDEa+/M7GtYcWg=";
        final String esk = "XFikMrFPxOG+NleCjE7MYZ4XrWgWELyjx8VJJnd/ErnTh/hUk++E0NIFG/PAzYj5HmauQNZbkxXxSvoGZ1DM5U/eCI0paNPzIn5S6W7IgUTT6/ll7vk2j9ZsU1qGrhRMcOZd3Uo+b78OMP58iX7fyofhfJpvhs5M2PbAPfculfLoNQq9OynVIrIDplGX8rF3FOeZ9kXJUnMQK9qrKhHEPer5PXqOb47kqdnVHfDu7cDbDzZc34YDFd7pCWbVhC8GPhfZ/CNUot9A1Wghu1ECg6VHvRSK9q31dk4dNziEBp7yz8M/B6E8HvYcRidHYxOq4Qe9tlB61KwLwLClInX0tiI25dXOV53ux2TS387la2O5MlfFpGV2je3d98UI6E5mSXNO7tlxfRoRDbNebai+HMZr2fuuZ4bztVNkRKhrJmA5CXNwESrv+Q==";
        final String raw = "2018-10-25 14:25:17,956|DEBUG|TIMER-LOG|New Secret Key generated. ";
        final String ls = "10000";
        final String tl = "300000";
        final String ts = "1540470317956";
        final String line = "{\"preview\":true,\"offset\":0,\"result\":{\"_raw\":\"2018-10-25 14:25:17,956|DEBUG|TIMER-LOG|New Secret Key generated. {*SG::LzlqCoyZBBJJAuMr/8YQV/5yAwCqURk6c5uBYhh2qehYyrGq8fG77vdhXc+StnX1NOvnfzmtHLcH5tYPW3w8bcFTHDeIU7nWd9ootsLwpPqHnRULZ97kqPc6i9s+hlmqivHRvs6X2QmhEoTDmuVtTMpbCu1q5svH315QWpLOXHB4g4gd6z4fS1Qs+K+HacIE/MitpNOcugSRBBfgwMK7yC0QbXUZ/hiCfOoXBI5RjaFMVxoFA0Rp1VysDtAkWrawby3P2e4ahmWkmD7P6BCrzhqh+TmaER1IJT+mr8ShUaidaZRFUQYAdyt5vNcrTPYJqxXNm0hGlPsr63pfaKoJkA==,LSK::Jd10KtoV11/a3XgJwIAU71K0PCjDBTDEa+/M7GtYcWg=,ESK::XFikMrFPxOG+NleCjE7MYZ4XrWgWELyjx8VJJnd/ErnTh/hUk++E0NIFG/PAzYj5HmauQNZbkxXxSvoGZ1DM5U/eCI0paNPzIn5S6W7IgUTT6/ll7vk2j9ZsU1qGrhRMcOZd3Uo+b78OMP58iX7fyofhfJpvhs5M2PbAPfculfLoNQq9OynVIrIDplGX8rF3FOeZ9kXJUnMQK9qrKhHEPer5PXqOb47kqdnVHfDu7cDbDzZc34YDFd7pCWbVhC8GPhfZ/CNUot9A1Wghu1ECg6VHvRSK9q31dk4dNziEBp7yz8M/B6E8HvYcRidHYxOq4Qe9tlB61KwLwLClInX0tiI25dXOV53ux2TS387la2O5MlfFpGV2je3d98UI6E5mSXNO7tlxfRoRDbNebai+HMZr2fuuZ4bztVNkRKhrJmA5CXNwESrv+Q==,PHMAC::qwQjgCt3g7+6MPQen2pB4weT1e/FgBvl2iRn/LOJ3IU=,LS::10000,TL::300000,TS::1540470317956,HMAC::cxEQsXjOFU09oL8gGvDMSC0TzYDwtGrJdfL27iOzNnQ=*}\",\"_time\":\"2018-10-25T14:25:17.956+0200\",\"host\":\"h002gn\",\"index\":\"it_evoting_cc\",\"linecount\":\"1\",\"source\":\"D:\\\\logs_3\\\\cv\\\\logs\\\\cv_secure-20181023-154513-8.log\",\"sourcetype\":\"post_evoting_securelogs\",\"splunk_server\":\"hin02a.pnet.ch\"}}";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            stream.write(Base64.decode(phmac));
            stream.write(Base64.decode(lsk));
            stream.write(Base64.decode(esk));
            //stream.writeInt(Integer.parseInt(ls));
            //stream.writeLong(Long.parseLong(tl));
            stream.writeLong(Long.parseLong(ts));
            stream.write(raw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }
}