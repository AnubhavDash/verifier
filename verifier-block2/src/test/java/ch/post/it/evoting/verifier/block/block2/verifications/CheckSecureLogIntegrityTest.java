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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class CheckSecureLogIntegrityTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSecureLogIntegrity();
    }

    @Test
    @Ignore
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSecureLogIntegrityTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("Check secure log integrity failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSecureLogIntegrityTest/NOK").toURI()));
    }

    @Test
    // TODO Extract this test to another class, as it is not testing CheckSecureLogIntegrity code
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
            Assert.fail(e.getMessage());
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }

    @Test
    // TODO Extract this test to another class, as it is not testing CheckSecureLogIntegrity code
    public void generateHmacRegularLog() {
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
            Assert.fail(e.getMessage());
        }

        String calculatedHmac = Base64.toBase64String(HmacGenerator.hash(bytes.toByteArray(), Base64.decode(endLsk)));

        Assert.assertEquals(hmac, calculatedHmac);
    }
}
