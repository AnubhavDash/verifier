package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import ch.post.it.evoting.verifier.dto.Option;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Test06 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test06.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test06.description"));
        def.setId(6);
        def.setName("isQuadraticResidue([vo])");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_CRYPTO_SETUP);
            EncryptionParameters encryptionParameters = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
            String pString = encryptionParameters.getP();
            BigInteger p = TypeConverter.stringToBigInteger(pString);

            path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            //votations
            Collection<Integer> errors = ballotBoxes.parallelStream()
                    .flatMap(bb -> bb.getCountingCircles().stream())
                    .flatMap(cc -> cc.getDomainOfInfluence().stream())
                    .flatMap(doi -> doi.getVotes().stream())
                    .flatMap(v -> v.getQuestions().stream())
                    .flatMap(q -> q.getOptions().stream())
                    .filter(o -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(o.getPrimeNumber()), p))
                    .map(Option::getPrimeNumber)
                    .collect(Collectors.toList());

            //lists
            errors.addAll(
                    ballotBoxes.parallelStream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .filter(l -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(l.getPrimeNumber()), p))
                            .map(ch.post.it.evoting.verifier.dto.List::getPrimeNumber)
                            .collect(Collectors.toList()));

            //candidates
            errors.addAll(
                    ballotBoxes.parallelStream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .flatMap(l -> l.getCandidatePositions().stream())
                            .flatMap(cp -> cp.getPrimeNumber().stream())
                            .filter(v -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(v), p))
                            .collect(Collectors.toList()));

            //candidates without lists
            errors.addAll(
                    ballotBoxes.stream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getCandidates().stream())
                            .flatMap(c -> c.getPrimeNumber().stream())
                            .filter(prime -> !MathHelper.isPrime(TypeConverter.integerToBigInteger(prime)))
                            .collect(Collectors.toList()));


            if (errors.isEmpty()) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test06.nok.message", errors.toString()));
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message"));
            } else {
                LOGGER.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
