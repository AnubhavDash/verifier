package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.configuration.locations.DefaultLocationNames;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamal;
import com.scytl.products.ov.mixnet.commons.io.BGReader;
import com.scytl.products.ov.mixnet.commons.io.ElGamalEncryptedBallotsLoader;
import com.scytl.products.ov.mixnet.commons.io.JSONProofsReader;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.tools.MatrixArranger;
import com.scytl.products.ov.mixnet.proofs.bg.ShuffleProofVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Test01 extends Test {

    private static Logger LOGGER = Logger.getLogger(Test01.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition definition = new TestDefinition();

        definition.setBlockId(3);
        definition.setId(1);
        definition.setCategory(Category.COMPLETENESS);
        definition.setName("checkShuffleArgument");
        definition.setDescription(null); //TODO set description

        return definition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path ballotBoxesPath = inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES);
            final File[] files = Arrays.stream(Objects.requireNonNull(ballotBoxesPath.toFile().listFiles(File::isDirectory)))
                    .flatMap(d -> Arrays.stream(Objects.requireNonNull(d.listFiles(File::isDirectory))))
                    .toArray(File[]::new);

            if (files != null) {
                for (File file : files) {
                    Path rootPath = file.toPath().getParent();
                    final String batchName = file.getName();
                    if (!batchName.equals("tally")) {
                        ZpGroup zpGroup = BGReader.createZpGroup(rootPath, batchName);
                        ElGamalPublicKey publicKey = BGReader.createElGamalPublicKey(batchName, rootPath);
                        GjosteenElGamal cryptosystem = new GjosteenElGamal(zpGroup, publicKey);
                        // System.out.println("path = "+outputParentPath.toString()+"/"+batchName);
                        LOGGER.debug("Ballots before mixing");
                        final ElGamalEncryptedBallots encryptedBallots =
                                ElGamalEncryptedBallotsLoader.loadCSV(zpGroup.getP(), zpGroup.getOrder(), rootPath, batchName,
                                        DefaultLocationNames.ENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);
                        if (encryptedBallots.getBallots().isEmpty()) {
                            LOGGER.info("0 ballots, nothing to mix!");
                            continue;
                        }

                        LOGGER.debug("Re-encrypted ballots");
                        final ElGamalEncryptedBallots reencryptedBallots = ElGamalEncryptedBallotsLoader.loadCSV(
                                zpGroup.getP(), zpGroup.getOrder(), rootPath, batchName,
                                DefaultLocationNames.REENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);
                        if (reencryptedBallots.getBallots().isEmpty()) {
                            LOGGER.info("0 ballots reencrypted, no mixing performed!");
                            continue;
                        }

                        JSONProofsReader proofsReader = new JSONProofsReader();
                        final ShuffleProof shuffleProof = proofsReader.read(rootPath, batchName);

                        final ShuffleProofVerifier shuffleProofVerifier = getVerifier(zpGroup, cryptosystem,
                                shuffleProof, rootPath, batchName, encryptedBallots, reencryptedBallots);

                        if (!shuffleProofVerifier.verifyProof(shuffleProof.getInitialMessage(),
                                shuffleProof.getFirstAnswer(), shuffleProof.getSecondAnswer())) {
                            throw new TestFailureException();
                        }
                    }
                }
            }
            result.setStatus(Status.OK);
        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setStatus(Status.NOK);
            } else {
                LOGGER.error("Unexpected error", e);
                result.setStatus(Status.NOK);
            }
        }
        return result;
    }

    private static ShuffleProofVerifier getVerifier(ZpGroup zpGroup, GjosteenElGamal cryptosystem,
                                                    ShuffleProof shuffleProof, Path outputParentPath, String batchName,
                                                    ElGamalEncryptedBallots encryptedBallots, ElGamalEncryptedBallots reencryptedBallots) throws IOException {

        final int N = encryptedBallots.getBallots().size();
        final int m = shuffleProof.getInitialMessage().length;
        final int n = N / m;

        LOGGER.debug("Configured n = " + n + " and m = " + m);

        final CommitmentParams commitmentParams =
                BGReader.createCommitmentParams(zpGroup, n, batchName, outputParentPath);

        final Ciphertext[][] encryptedBallotsCiphertext =
                MatrixArranger.arrangeInCiphertextMatrix(encryptedBallots, m, n);

        final Ciphertext[][] reencryptedBallotsCiphertext =
                MatrixArranger.arrangeInCiphertextMatrix(reencryptedBallots, m, n);

        return new ShuffleProofVerifier(cryptosystem, commitmentParams, encryptedBallotsCiphertext,
                reencryptedBallotsCiphertext);
    }
}
