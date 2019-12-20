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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

public class IsMemberOfGroupPKEATest {
    private IsMemberOfGroupPKEA isMemberOfGroupPKEA;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        isMemberOfGroupPKEA = new IsMemberOfGroupPKEA();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult = isMemberOfGroupPKEA.verify(new File(getClass().getResource("/IsMemberOfGroupPKEATest/OK").getFile()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("No such Elements was found in the publicKey");
        isMemberOfGroupPKEA.verify(new File(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK").getFile()));
    }

    @Test
    public void executeTestNOK2() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("Euler criterion does not equal to 1");
        isMemberOfGroupPKEA.verify(new File(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK2").getFile()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(IOException.class);
        exceptionRule.expectMessage("electoralAuthority\\.json");
        isMemberOfGroupPKEA.verify(new File(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK-NOFILE").getFile()));
    }

//    @Test
//    public void test() throws Exception {
////        BigInteger bi = new BigInteger("685917403914445899847861871214278244603510114266447639238304076733805287973349233472738326702032813895211218172219805351544772253788843872085537930643873004596729468079091245896091391100876302537230201848062765283464700319547677309688559052263029600449996931247500319766283960225974918943265478976709836674372928123682124468385317782431498201372655143433878248908567146477572830603474574542180752577228209645992211443829089760116830858557879398139015516551857024669852260655210865623141520355205146286142607398363276208384187573269628340951780121020714919199186544585859735928644753711130159995733003667795333605");
////        String element = "BW76rE6ARTpNv+5RCLK35jwbDUW0X9KylssTEZ8xEAVcTSetCfy7YPSRDhQnpqQOQyWEOWDyfuHB+rxn3hQK3/V8AoMJ7fHWN6/GNJaJlP318QQdnaTVevbIiAYOn+WWjGwggDOhpYiv749oFejGHkxBNzoayQLwjRvvhIObfP/xoaL117G0Vb2mA7Q86qJzRM7SROhzYZo0XqFB7s9Fd76p+ZnGOQ4PUv5t2pjlgPqe6PNXtf663QCx0aP9uAJrzWQw1xH59I7DbTd6IfQU0vTO1ltxvQcLKAwSp+68AkBOt2O7edyoE7AWaE7b/UtRyV1oZfl4v/IOibUURivm2g==";
////        BigInteger big = TypeConverter.byteToBigInteger(TypeConverter.base64ToByte(element));
////        System.out.println(big);
////
////        String result = TypeConverter.bigIntegerToB64String(bi);
////        System.out.println(result);
////        Assert.assertEquals(element, result);
//
//
//        String publicKeyB64 = "eyJwdWJsaWNLZXkiOnsienBTdWJncm91cCI6eyJnIjoiQWc9PSIsInAiOiJBSUd0OHd6aXp5SjgraWE3MTZZZ1k0RmpHNWdKUmpmeitpQjNPZVdFTUxDT0xub1dHMHdId0ZReW8yVk54NGxnQlBEbVNJYjVOdEovMTJVM2RmZTdKZGdZakVNU2F6OUhVcmZvd0hOd0N1RDFWVm1EWHhVdnl4UFNWNS9pc3M3YTg1c29wdlI5bjA5aW12dnA4WERmUE51Y2NkMTBUUXdHU0tWdDJ2Tjd2U01yZ1FPc0dFdUJCc0Vtc2RsUmlsNCt4OU5UWXBoc2MvN3ZUVjlwUk40VStaSTV5VGJCb2Y2U0ZjSDVtd0FhdGxaQi9SenR3QTF3aDNxVVVneUx6L2tUS3FUUmJhUGM1VzErTERQQlpuTWlhVFdxNzg2NDE4cGlvNG9NODZtVkl0aGZFLzM0UzBrbjhHcEpHek5GMTJOemJWUXpicHY2WXBtclBLRzFGZmFRUHdjPSIsInEiOiJRTmI1aG5GbmtUNTlFMTNyMHhBeHdMR056QVNqRy9uOUVEdWM4c0lZV0VjWFBRc05wZ1BnS2hsUnNxYmp4TEFDZUhNa1EzeWJhVC9yc3B1Nis5MlM3QXhHSVlrMW42T3BXL1JnT2JnRmNIcXFyTUd2aXBmbGlla3J6L0ZaWjIxNXpaUlRlajdQcDdGTmZmVDR1RytlYmM0NDdyb21oZ01rVXJidGViM2VrWlhBZ2RZTUpjQ0RZSk5ZN0tqRkx4OWo2YW14VERZNS8zZW1yN1NpYndwOHlSemttMkRRLzBrSzRQek5nQTFiS3lEK2puYmdCcmhEdlVvcEJrWG4vSW1WVW1pMjBlNXl0cjhXR2VDek9aRTBtdFYzNTF4cjVURlJ4UVo1MU1xUmJDK0ovdndscEpQNE5TU05tYUxyc2JtMnFobTNUZjB4VE5XZVVOcUsrMGdmZ3c9PSJ9LCJlbGVtZW50cyI6WyJCVzc2ckU2QVJUcE52KzVSQ0xLMzVqd2JEVVcwWDlLeWxzc1RFWjh4RUFWY1RTZXRDZnk3WVBTUkRoUW5wcVFPUXlXRU9XRHlmdUhCK3J4bjNoUUszL1Y4QW9NSjdmSFdONi9HTkphSmxQMzE4UVFkbmFUVmV2YklpQVlPbitXV2pHd2dnRE9ocFlpdjc0OW9GZWpHSGt4Qk56b2F5UUx3alJ2dmhJT2JmUC94b2FMMTE3RzBWYjJtQTdRODZxSnpSTTdTUk9oellabzBYcUZCN3M5RmQ3NnArWm5HT1E0UFV2NXQycGpsZ1BxZTZQTlh0ZjY2M1FDeDBhUDl1QUpyeldRdzF4SDU5STdEYlRkNklmUVUwdlRPMWx0eHZRY0xLQXdTcCs2OEFrQk90Mk83ZWR5b0U3QVdhRTdiL1V0UnlWMW9aZmw0di9JT2liVVVSaXZtMmc9PSIsIkFyZUNnZE51N0RxT1FIalhIU0JYdkplV2FYYWdaeW5SVEN1Sm5tNXBRazcvUHNmRGpUUTJpYi92K09RZmR2WEJ3UTdQUHBIOGNEYTZZOGZRSEVJOG1WTGdJc1c5dWtnR2p4NEZRSlRUc1hKMitpSWFvTHFrbkVaMkMvT0traUhyMTB2YTJySzY1RHF5VnN2UVZKS2ZjeXRkN3BScEVWUlI4bDFiU3ZUbkFwS0VaS0Jac3F2WnkxWk9NNG1IVEdKUGNibFFQMjVwR3lmVndkeUh6QVEzV1dFN2ViTU16aEFLSkNQRVdPdUpDMzJHODNWTTF1WTl0ODQyMndWdlhCbjNiTE96VVRDTXNkTDQ0clVLbm9DbjV3dTc3TGdxOFlOSUVwcGxtYjJ4ZUFPSzlIY0VOT25sQVdCcm5nRzRUV2VVRi96aFpkeEdqNVBnVWpOM3cvNE9Kdz09IiwiU1UwMlZodkFMZ1pZbnBnS0Z6a3pXSlNMb1l6VlU0YVhiT2RoQnZIei83ZjlpY3lydCs0T3d5RnVRTnlUV0xoNHVSVHhQR0RaMFduTTFXMUNSS0pVM2RZTTNMRk5FT1pHNmJSbmJpeDJIWGc2dFZPcEpBbDYzNVBpaXpjcU5oRTNrWmxBZi9OTm05a1luYUE0ZE9WamxIS0ovdWZrOXVwOFY4SmRrTVdISlNZVyt3SExZb0pGeW1YdHFMTjZLNjdOQUxHNEtjYVQyc2l5M0lQV09pU3Zxc2JDNGlvdTN2NitOcGExZVdLMDB0Z0hNTGVJZW1VVTByczQ1QXpkRDZVaEdJVllvYlNkUG43cVBhMnJwSjFxQ0dWc1ZpYUJuQVZEU1ZRMEh3RXcwblVxNkk3Qi9hQWRXanBHL1VLWjhiL3BaVUFTNklZR053WVE0ZU05V1NiMU5RPT0iXX19";
//        byte[] decoded = TypeConverter.base64ToByte(publicKeyB64);
//        String publicKey = TypeConverter.byteToString(decoded);
//        System.out.println(publicKey);
//
//        String publicKey_2 = "{\"publicKey\":{\"zpSubgroup\":{\"g\":\"Ag==\",\"p\":\"AIGt8wzizyJ8+ia716YgY4FjG5gJRjfz+iB3OeWEMLCOLnoWG0wHwFQyo2VNx4lgBPDmSIb5NtJ/12U3dfe7JdgYjEMSaz9HUrfowHNwCuD1VVmDXxUvyxPSV5/iss7a85sopvR9n09imvvp8XDfPNuccd10TQwGSKVt2vN7vSMrgQOsGEuBBsEmsdlRil4+x9NTYphsc/7vTV9pRN4U+ZI5yTbBof6SFcH5mwAatlZB/RztwA1wh3qUUgyLz/kTKqTRbaPc5W1+LDPBZnMiaTWq786418pio4oM86mVIthfE/34S0kn8GpJGzNF12NzbVQzbpv6YpmrPKG1FfaQPwc=\",\"q\":\"QNb5hnFnkT59E13r0xAxwLGNzASjG/n9EDuc8sIYWEcXPQsNpgPgKhlRsqbjxLACeHMkQ3ybaT/rspu6+92S7AxGIYk1n6OpW/RgObgFcHqqrMGvipfliekrz/FZZ215zZRTej7Pp7FNffT4uG+ebc447romhgMkUrbteb3ekZXAgdYMJcCDYJNY7KjFLx9j6amxTDY5/3emr7Sibwp8yRzkm2DQ/0kK4PzNgA1bKyD+jnbgBrhDvUopBkXn/ImVUmi20e5ytr8WGeCzOZE0mtV351xr5TFRxQZ51MqRbC+J/vwlpJP4NSSNmaLrsbm2qhm3Tf0xTNWeUNqK+0gfgw==\"},\"elements\":[\"AWQXNlm6b+4oYklMCmsxIFDsTSAuLQK9FCvLiaGVxqlO7Nb1AII8QcfTNs3dGDph2/EHObJrIoeeM+aA/XXFWA2xJ44XBZ4qjTtVjnyNt4zKOZqnU11lwUVSDleRDk9BKheVodf4OsK8ZkmTelhDGjAxdpYK8/iPgEiwASfV4o9Ye0yIqefcPujeuUSrPh1MnOD6b9k0EMtQ/ZnsA8qQwn6iTC+O57Gt6DC/9mzGBFi8Iw8xE/d7nO2+7fzs++gLNqDRHG8ZE5lIjqXBDtYJbU0BwBdo5J1zfvzyWAv2yUfCOzxY9DBLy7IyytSzMoHfsHo3ZeGoIMEJ3Z7/C9Hl\",\"AreCgdNu7DqOQHjXHSBXvJeWaXagZynRTCuJnm5pQk7/PsfDjTQ2ib/v+OQfdvXBwQ7PPpH8cDa6Y8fQHEI8mVLgIsW9ukgGjx4FQJTTsXJ2+iIaoLqknEZ2C/OKkiHr10va2rK65DqyVsvQVJKfcytd7pRpEVRR8l1bSvTnApKEZKBZsqvZy1ZOM4mHTGJPcblQP25pGyfVwdyHzAQ3WWE7ebMMzhAKJCPEWOuJC32G83VM1uY9t8422wVvXBn3bLOzUTCMsdL44rUKnoCn5wu77Lgq8YNIEpplmb2xeAOK9HcENOnlAWBrngG4TWeUF/zhZdxGj5PgUjN3w/4OJw==\",\"SU02VhvALgZYnpgKFzkzWJSLoYzVU4aXbOdhBvHz/7f9icyrt+4OwyFuQNyTWLh4uRTxPGDZ0WnM1W1CRKJU3dYM3LFNEOZG6bRnbix2HXg6tVOpJAl635PiizcqNhE3kZlAf/NNm9kYnaA4dOVjlHKJ/ufk9up8V8JdkMWHJSYW+wHLYoJFymXtqLN6K67NALG4KcaT2siy3IPWOiSvqsbC4iou3v6+Npa1eWK00tgHMLeIemUU0rs45AzdD6UhGIVYobSdPn7qPa2rpJ1qCGVsViaBnAVDSVQ0HwEw0nUq6I7B/aAdWjpG/UKZ8b/pZUAS6IYGNwYQ4eM9WSb1NQ==\"]}}";
//        byte[] encoded = TypeConverter.stringToByte(publicKey_2);
//        String publicKeyB64_2 = TypeConverter.byteToBase64String(encoded);
//        System.out.println(publicKeyB64_2);
//        Assert.assertEquals(publicKeyB64, publicKeyB64_2);
//
//    }
}
