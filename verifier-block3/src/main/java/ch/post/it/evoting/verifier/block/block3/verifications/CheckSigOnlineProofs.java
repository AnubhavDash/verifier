/*
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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.BallotBoxId;
import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.OnlineMixing;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CheckSigOnlineProofs extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setId(75);
        def.setName("checkSigOnlineProofs");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.description"));
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) {
        VerificationResult result = new VerificationResult();
        result.setStatus(Status.NA);
        /* Temporary commented - feedback about original sentence needed
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectoryPath.resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                if (onlineMixings == null || onlineMixings.length != 3) {
                    throw new TestFailureException("the number of control components expected is 3 but actual is " + (onlineMixings == null ? "0" : onlineMixings.length));
                }

                byte[] platformRootCA = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath.resolve(Block3TestSuite.PATH_CERTIFICATES).toFile(), "platformRootCA.*\\.pem").toPath());

                for (File file : onlineMixings) {

                    OnlineMixingProofLoader loader = new OnlineMixingProofLoader(file.toPath());
                    OnlineMixing onlineMixing = loader.getOnlineMixing();

                    Signature sig = onlineMixing.getSignature();
                    List<String> certificateChain = sig.getCertificateChain();
                    String certificate = certificateChain.get(0);
                    byte[][] intermediates = certificateChain.stream().skip(1).map(str -> str.getBytes(StandardCharsets.UTF_8)).toArray(byte[][]::new);
                    String signature = sig.getSignatureContents();

                    String content = OnlineProofSignatureSourceBuilder.build(onlineMixing, loader.getShuffleProof());

                    boolean success = SignatureChecker.verifySignature(content.getBytes(StandardCharsets.UTF_8),
                            TypeConverter.base64ToByte(signature),
                            certificate.getBytes(StandardCharsets.UTF_8),
                            intermediates,
                            platformRootCA);

                    if (!success) {
                        result.setStatus(Status.NOK);
                        result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "verification75.nok.message", file.getName()));
                        return result;
                    }
                }
                result.setStatus(Status.OK);
            }
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "verification75.ccnumber.nok.message"));
        } catch (IOException e) {
            LOGGER.error("File not found", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "verification75.file.not.found.message", e.getLocalizedMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }*/
        return result;
    }


    /*
     * This class build the original content which was used to be signed from the given onlineMixing and shuffleProof parameters
     */
    static class OnlineProofSignatureSourceBuilder {
        static String build(OnlineMixing onlineMixing, ShuffleProof shuffleProof) {
            BallotBoxId ballotBoxId = onlineMixing.getVoteSetId().getBallotBoxId();

            String voteEnc = String.format("{\"publicKey\":{\"zpSubgroup\":{\"g\":\"%s\",\"p\":\"%s\",\"q\":\"%s\"},\"elements\":[%s]}}",
                    TypeConverter.bigIntegerToB64String(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getG()),
                    TypeConverter.bigIntegerToB64String(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getP()),
                    TypeConverter.bigIntegerToB64String(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getQ()),
                    String.join(",", onlineMixing.getVoteEncryptionKey().getElements().stream().map(b -> String.format("\"%s\"", TypeConverter.bigIntegerToB64String(b))).collect(Collectors.toList()))
            );

            String prevVoteEnc = String.format("{\"publicKey\":{\"zpSubgroup\":{\"g\":\"%s\",\"p\":\"%s\",\"q\":\"%s\"},\"elements\":[%s]}}",
                    TypeConverter.bigIntegerToB64String(onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getG()),
                    TypeConverter.bigIntegerToB64String(onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getP()),
                    TypeConverter.bigIntegerToB64String(onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getQ()),
                    String.join(",", onlineMixing.getPreviousVoteEncryptionKey().getElements().stream().map(b -> String.format("\"%s\"", TypeConverter.bigIntegerToB64String(b))).collect(Collectors.toList()))
            );

            return String.format("%s-%s-%s-%d%s%s%s%s%s%s%s%s%s",
                    ballotBoxId.getTenantId(),
                    ballotBoxId.getElectionEventId(),
                    ballotBoxId.getId(),
                    onlineMixing.getVoteSetId().getIndex(),
                    onlineMixing.getVotes().stream()
                            .map(vote -> vote.getGamma() + ";" + vote.getPhis().stream().map(BigInteger::toString).collect(Collectors.joining()))
                            .collect(Collectors.joining()),
                    voteEnc,
                    onlineMixing.getCommitmentParameters().stream().map(BigInteger::toString).collect(Collectors.joining()),
                    onlineMixing.getDecryptionProofs().stream().collect(Collectors.joining()),
                    onlineMixing.getShuffledVotes().stream()
                            .map(vote -> vote.getGamma() + ";" + vote.getPhis().stream().map(BigInteger::toString).collect(Collectors.joining()))
                            .collect(Collectors.joining()),
                    shuffleProof != null ? serialize(shuffleProof) : "",
                    onlineMixing.getTimestamp(),
                    onlineMixing.getPreviousVotes().stream()
                            .map(vote -> vote.getGamma() + ";" + vote.getPhis().stream().map(BigInteger::toString).collect(Collectors.joining()))
                            .collect(Collectors.joining()),
                    prevVoteEnc);
        }

        private static String serialize(ShuffleProof proof) {
            return String.format("{\n  \"initialMessage\" : [ %s ],\n  \"firstAnswer\" : [ %s ],\n  \"secondAnswer\" : {\n    \"msgPA\" : {\n      \"commitmentPublicB\" : %s,\n      \"iniSVA\" : {\n        \"commitmentPublicD\" : %s,\n        \"commitmentPublicLowDelta\" : %s,\n        \"commitmentPublicHighDelta\" : %s\n      },\n      \"ansSVA\" : {\n        \"exponentsTildeA\" : [ %s ],\n        \"exponentsTildeB\" : [ %s ],\n        \"exponentTildeR\" : %s,\n        \"exponentTildeS\" : %s\n      },\n      \"iniHPA\" : {\n        \"commitmentPublicB\" : [ %s ]\n      },\n      \"ansHPA\" : {\n        \"initial\" : {\n          \"commitmentPublicA0\" : %s,\n          \"commitmentPublicBM\" : %s,\n          \"commitmentPublicD\" : [ %s ]\n        },\n        \"answer\" : {\n          \"exponentsA\" : [ %s ],\n          \"exponentsB\" : [ %s ],\n          \"exponentR\" : %s,\n          \"exponentS\" : %s,\n          \"exponentT\" : %s\n        }\n      }\n    },\n    \"iniMEBasic\" : {\n      \"commitmentPublicA0\" : %s,\n      \"commitmentPublicB\" : [ %s ],\n      \"ciphertextsE\" : [ %s ]\n    },\n    \"ansMEBasic\" : {\n      \"exponentsA\" : [ %s ],\n      \"exponentR\" : %s,\n      \"exponentsB\" : %s,\n      \"exponentS\" : %s,\n      \"randomnessTau\" : %s\n    }\n  }\n}",
                    Arrays.stream(proof.getInitialMessage()).map(pc -> serialize(pc)).collect(Collectors.joining(",")),
                    Arrays.stream(proof.getFirstAnswer()).map(pc -> serialize(pc)).collect(Collectors.joining(",")),
                    serializeWithOneIndent(proof.getSecondAnswer().getMsgPA().getCommitmentPublicB()),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getIniSVA().getCommitmentPublicD()),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getIniSVA().getCommitmentPublicLowDelta()),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getIniSVA().getCommitmentPublicHighDelta()),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getAnsSVA().getExponentsTildeA()).map(e -> serializeWithOneIndent(e)).collect(Collectors.joining(", ")),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getAnsSVA().getExponentsTildeB()).map(e -> serializeWithOneIndent(e)).collect(Collectors.joining(", ")),
                    serializeWithOneIndent(proof.getSecondAnswer().getMsgPA().getAnsSVA().getExponentTildeR()),
                    serializeWithOneIndent(proof.getSecondAnswer().getMsgPA().getAnsSVA().getExponentTildeS()),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getIniHPA().getCommitmentPublicB()).map(pc -> serializeWithTwoIndents(pc)).collect(Collectors.joining(", ")),
                    serializeWithThreeIndents(proof.getSecondAnswer().getMsgPA().getAnsHPA().getInitial().getCommitmentPublicA0()),
                    serializeWithThreeIndents(proof.getSecondAnswer().getMsgPA().getAnsHPA().getInitial().getCommitmentPublicBM()),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getAnsHPA().getInitial().getCommitmentPublicD()).map(pc -> serializeWithThreeIndents(pc)).collect(Collectors.joining(", ")),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getAnsHPA().getAnswer().getExponentsA()).map(e -> serializeWithTwoIndents(e)).collect(Collectors.joining(", ")),
                    Arrays.stream(proof.getSecondAnswer().getMsgPA().getAnsHPA().getAnswer().getExponentsB()).map(e -> serializeWithTwoIndents(e)).collect(Collectors.joining(", ")),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getAnsHPA().getAnswer().getExponentR()),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getAnsHPA().getAnswer().getExponentS()),
                    serializeWithTwoIndents(proof.getSecondAnswer().getMsgPA().getAnsHPA().getAnswer().getExponentT()),
                    serializeWithOneIndent(proof.getSecondAnswer().getIniMEBasic().getCommitmentPublicA0()),
                    Arrays.stream(proof.getSecondAnswer().getIniMEBasic().getCommitmentPublicB()).map(pc -> serializeWithOneIndent(pc)).collect(Collectors.joining(", ")),
                    Arrays.stream(proof.getSecondAnswer().getIniMEBasic().getCiphertextsE()).map(c -> serialize(c)).collect(Collectors.joining(", ")),
                    Arrays.stream(proof.getSecondAnswer().getAnsMEBasic().getExponentsA()).map(e -> serialize(e)).collect(Collectors.joining(", ")),
                    serialize(proof.getSecondAnswer().getAnsMEBasic().getExponentR()),
                    serialize(proof.getSecondAnswer().getAnsMEBasic().getExponentB()),
                    serialize(proof.getSecondAnswer().getAnsMEBasic().getExponentS()),
                    serialize(proof.getSecondAnswer().getAnsMEBasic().getRandomnessTau())
            );
        }


        private static String serialize(PublicCommitment pc) {
            return String.format("{\n    \"element\" : {\n      \"value\" : %s,\n      \"p\" : %s,\n      \"q\" : %s\n    }\n  }",
                    pc.getElement().getValue(),
                    pc.getElement().getParams().getP(),
                    pc.getElement().getParams().getOrder());
        }

        private static String serializeWithOneIndent(PublicCommitment pc) {
            return String.format("{\n        \"element\" : {\n          \"value\" : %s,\n          \"p\" : %s,\n          \"q\" : %s\n        }\n      }",
                    pc.getElement().getValue(),
                    pc.getElement().getParams().getP(),
                    pc.getElement().getParams().getOrder());
        }

        private static String serializeWithTwoIndents(PublicCommitment pc) {
            return String.format("{\n          \"element\" : {\n            \"value\" : %s,\n            \"p\" : %s,\n            \"q\" : %s\n          }\n        }",
                    pc.getElement().getValue(),
                    pc.getElement().getParams().getP(),
                    pc.getElement().getParams().getOrder());
        }

        private static String serializeWithThreeIndents(PublicCommitment pc) {
            return String.format("{\n            \"element\" : {\n              \"value\" : %s,\n              \"p\" : %s,\n              \"q\" : %s\n            }\n          }",
                    pc.getElement().getValue(),
                    pc.getElement().getParams().getP(),
                    pc.getElement().getParams().getOrder());
        }

        private static String serialize(Exponent e) {
            return String.format("{\n        \"q\" : %s,\n        \"value\" : %s\n      }",
                    e.getOrder(),
                    e.getValue());
        }

        private static String serializeWithOneIndent(Exponent e) {
            return String.format("{\n          \"q\" : %s,\n          \"value\" : %s\n        }",
                    e.getOrder(),
                    e.getValue());
        }

        private static String serializeWithTwoIndents(Exponent e) {
            return String.format("{\n            \"q\" : %s,\n            \"value\" : %s\n          }",
                    e.getOrder(),
                    e.getValue());
        }

        private static String serialize(Ciphertext c) {
            return String.format("{\n        \"gamma\" : \"%s\",\n        \"phis\" : \"%s\"\n      }",
                    String.valueOf(c.getParts()[0].getValue() + ";" + c.getParts()[0].getParams().getP() + ";" + c.getParts()[0].getParams().getOrder()),
                    String.valueOf(Arrays.asList(c.getParts()).subList(1, c.getParts().length).get(0).getValue()) + ";" + c.getParts()[0].getParams().getP() + ";" + c.getParts()[0].getParams().getOrder());
        }

        private static String serialize(Randomness r) {
            return String.format("{\n        \"class\" : \"%s\",\n        \"randomnessValue\" : %s\n      }",
                    String.valueOf(r.getClass().getTypeName()),
                    String.valueOf(serializeWithOneIndent(r.getExponent())));
        }

    }
}
