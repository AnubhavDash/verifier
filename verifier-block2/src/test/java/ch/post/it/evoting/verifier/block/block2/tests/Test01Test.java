package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test01Test {

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
    @Ignore
    public void reverseFile() throws IOException {
        Path path = new File(getClass().getResource("/Test01/OK/secureLogs/Evoting_CC_verifier_export_7d.json").getFile()).toPath();
        Stream<String> stream = Files.lines(path);
        List<String> collect = stream.collect(Collectors.toList());
        File file = new File("c:\\temp\\result.json");
        FileWriter fw = new FileWriter("c:\\temp\\result.json");
        for (int i = collect.size() - 1; i >= 0; i--) {
            fw.write(collect.get(i) + "\r\n");
        }
        fw.close();
    }

    @Test
    public void generateHmacCheckpoint() {
        final String endLsk = "U9LwEJ1oDitWij/tX8SJ44FqDGrFFhJXXr+Nakj509w=";
        final String hmac = "cxEQsXjOFU09oL8gGvDMSC0TzYDwtGrJdfL27iOzNnQ=";
        final String phmac = "qwQjgCt3g7+6MPQen2pB4weT1e/FgBvl2iRn/LOJ3IU=";
        final String lsk = "Jd10KtoV11/a3XgJwIAU71K0PCjDBTDEa+/M7GtYcWg=";
        final String esk = "XFikMrFPxOG+NleCjE7MYZ4XrWgWELyjx8VJJnd/ErnTh/hUk++E0NIFG/PAzYj5HmauQNZbkxXxSvoGZ1DM5U/eCI0paNPzIn5S6W7IgUTT6/ll7vk2j9ZsU1qGrhRMcOZd3Uo+b78OMP58iX7fyofhfJpvhs5M2PbAPfculfLoNQq9OynVIrIDplGX8rF3FOeZ9kXJUnMQK9qrKhHEPer5PXqOb47kqdnVHfDu7cDbDzZc34YDFd7pCWbVhC8GPhfZ/CNUot9A1Wghu1ECg6VHvRSK9q31dk4dNziEBp7yz8M/B6E8HvYcRidHYxOq4Qe9tlB61KwLwLClInX0tiI25dXOV53ux2TS387la2O5MlfFpGV2je3d98UI6E5mSXNO7tlxfRoRDbNebai+HMZr2fuuZ4bztVNkRKhrJmA5CXNwESrv+Q==";
        final String raw = "2018-10-25 14:25:17,956|DEBUG|TIMER-LOG|New Secret Key generated.\n";
        final String ls = "10000";
        final String tl = "300000";
        final String ts = "1540470317956";
        final String line = "{\"preview\":true,\"offset\":0,\"result\":{\"_raw\":\"2018-10-25 14:25:17,956|DEBUG|TIMER-LOG|New Secret Key generated. {*SG::LzlqCoyZBBJJAuMr/8YQV/5yAwCqURk6c5uBYhh2qehYyrGq8fG77vdhXc+StnX1NOvnfzmtHLcH5tYPW3w8bcFTHDeIU7nWd9ootsLwpPqHnRULZ97kqPc6i9s+hlmqivHRvs6X2QmhEoTDmuVtTMpbCu1q5svH315QWpLOXHB4g4gd6z4fS1Qs+K+HacIE/MitpNOcugSRBBfgwMK7yC0QbXUZ/hiCfOoXBI5RjaFMVxoFA0Rp1VysDtAkWrawby3P2e4ahmWkmD7P6BCrzhqh+TmaER1IJT+mr8ShUaidaZRFUQYAdyt5vNcrTPYJqxXNm0hGlPsr63pfaKoJkA==,LSK::Jd10KtoV11/a3XgJwIAU71K0PCjDBTDEa+/M7GtYcWg=,ESK::XFikMrFPxOG+NleCjE7MYZ4XrWgWELyjx8VJJnd/ErnTh/hUk++E0NIFG/PAzYj5HmauQNZbkxXxSvoGZ1DM5U/eCI0paNPzIn5S6W7IgUTT6/ll7vk2j9ZsU1qGrhRMcOZd3Uo+b78OMP58iX7fyofhfJpvhs5M2PbAPfculfLoNQq9OynVIrIDplGX8rF3FOeZ9kXJUnMQK9qrKhHEPer5PXqOb47kqdnVHfDu7cDbDzZc34YDFd7pCWbVhC8GPhfZ/CNUot9A1Wghu1ECg6VHvRSK9q31dk4dNziEBp7yz8M/B6E8HvYcRidHYxOq4Qe9tlB61KwLwLClInX0tiI25dXOV53ux2TS387la2O5MlfFpGV2je3d98UI6E5mSXNO7tlxfRoRDbNebai+HMZr2fuuZ4bztVNkRKhrJmA5CXNwESrv+Q==,PHMAC::qwQjgCt3g7+6MPQen2pB4weT1e/FgBvl2iRn/LOJ3IU=,LS::10000,TL::300000,TS::1540470317956,HMAC::cxEQsXjOFU09oL8gGvDMSC0TzYDwtGrJdfL27iOzNnQ=*}\",\"_time\":\"2018-10-25T14:25:17.956+0200\",\"host\":\"h002gn\",\"index\":\"it_evoting_cc\",\"linecount\":\"1\",\"source\":\"D:\\\\logs_3\\\\cv\\\\logs\\\\cv_secure-20181023-154513-8.log\",\"sourcetype\":\"post_evoting_securelogs\",\"splunk_server\":\"hin02a.pnet.ch\"}}";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            stream.write(Base64.decode(phmac));
            stream.write(Base64.decode(lsk));
            stream.write(Base64.decode(esk));
            if (StringUtils.isNotEmpty(ls)) stream.writeInt(Integer.parseInt(ls));
            if (StringUtils.isNotEmpty(tl)) stream.writeLong(Long.parseLong(tl));
            stream.writeLong(Long.parseLong(ts));
            stream.write(raw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }

    @Test
    public void generateHmacRegularLog1() {
        final String endLsk = "MD+KuCAgFCcbqETTqDeI79Fr9P3TMq2lpGfuahpZGp8=";
        final String hmac = "gtGMUiyEZlA/MeVMJAMbSmUj3DYjOkxWm7N+4i9kiZc=";
        final String phmac = "K2d+ArwhI/x6lSzFqpSc4f3AyxSLDK3109J/oMrEh7Y=";
        final String lsk = "";
        final String esk = "";
        final String raw = "2018-10-25 14:29:44,113|DEBUG|pool-1-thread-57|Log File Name: /data/logs/cg/./logs/cg_secure-20181025-142944-25.log\n";
        final String ls = "";
        final String tl = "";
        final String ts = "1540470584113";

        //2018-10-25 14:29:44,113|DEBUG|pool-1-thread-57|Log File Name: /data/logs/cg/./logs/cg_secure-20181025-142944-25.log {*TS::1540470584113,HMAC::gtGMUiyEZlA/MeVMJAMbSmUj3DYjOkxWm7N+4i9kiZc=*}"}}

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            stream.write(Base64.decode(phmac));
            stream.write(Base64.decode(lsk));
            stream.write(Base64.decode(esk));
            if (StringUtils.isNotEmpty(ls)) stream.writeInt(Integer.parseInt(ls));
            if (StringUtils.isNotEmpty(tl)) stream.writeLong(Long.parseLong(tl));
            stream.writeLong(Long.parseLong(ts));
            stream.write(raw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }

    @Test
    public void generateHmacRegularLog2() {
        final String endLsk = "MD+KuCAgFCcbqETTqDeI79Fr9P3TMq2lpGfuahpZGp8=";
        final String hmac = "JyFYPX1uQVvbEom4GoJE9fcjclMyinscUv1W3tbFp5M=";
        final String phmac = "gtGMUiyEZlA/MeVMJAMbSmUj3DYjOkxWm7N+4i9kiZc=";
        final String lsk = "";
        final String esk = "";
        final String raw = "2018-10-25 14:29:44,105|INFO|pool-1-thread-57|767905|serverIP|clientIP|tenantID|OV|CCGEN||GENCCCRT|-|-|000|-|7d6e16ea88564b0b864785f2b12bd5df|Control Component Signing Certificate successfully generated|#ccx_id=\"ccn_c2\" #request_id=\"N5H27T56RFX5NUOC\" #issuer_cn=\"CCN_C2 CA\" #pubkey_ids=\"DT3MljLgNROZtzyfsEewoQd5DtkqxQi3VpabeIk8BiM=\" #cert_cn=\"7d6e16ea88564b0b864785f2b12bd5df\" #cert_sn=\"648718858650821711427443097450587215666310197001\" \n";
        final String ls = "";
        final String tl = "";
        final String ts = "1540470584113";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            stream.write(Base64.decode(phmac));
            stream.write(Base64.decode(lsk));
            stream.write(Base64.decode(esk));
            if (StringUtils.isNotEmpty(ls)) stream.writeInt(Integer.parseInt(ls));
            if (StringUtils.isNotEmpty(tl)) stream.writeLong(Long.parseLong(tl));
            stream.writeLong(Long.parseLong(ts));
            stream.write(raw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }


    @Test
    @Ignore
    public void updateJSONFile() throws IOException {
        final String dir = "C:\\work\\projects\\verifier\\verifier-block2\\src\\test\\resources\\Test04\\OK\\secureLogs";
        Stream<String> lines = Files.lines(Paths.get(dir, "Evoting_CC_verifier_export_7d.json"));

        FileOutputStream fos = new FileOutputStream(Paths.get(dir, "result.json").toFile());

        lines.map(l -> {
            try {
                StringBuffer result = new StringBuffer();
                int evIndex = l.indexOf("\"ev\"");
                int endHostIndex = l.indexOf("|");
                String host = l.substring(evIndex + 6, endHostIndex);
                result.append(l, 0, evIndex);
                result.append("\"_raw\":\"");
                int endMetadataIndex = l.indexOf("*}\"") + 3;
                if (endMetadataIndex == 2) endMetadataIndex = l.length() -2;
                result.append(l, endHostIndex + 1, endMetadataIndex);
                result.append(", \"host\":\"" + host + "\"");
                result.append("}}\n");
                return result.toString();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).forEach(l -> {
            try {
                fos.write(l.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fos.close();

    }


}