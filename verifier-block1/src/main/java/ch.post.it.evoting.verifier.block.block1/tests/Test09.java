/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.dto.*;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.Option;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test09 of Block1, Step checkPrimeNumberOptions([vo])
 */
public class Test09 extends Test {

    private static final Logger log = Logger.getLogger(Test09.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.description"));
        def.setId(9);
        def.setName("checkPrimeNumberOptions([vo])");
        return def;
    }

    // common method used to verify if a vo is in error
    // if Euler criterion is not equals to 1 there is an error
    private boolean isBigIntInError(BigInteger vo, BigInteger p) {
        boolean inError = false;
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(new BigInteger("2"));
        BigInteger ec = vo.modPow(exponent, p);
        inError = !ec.equals(BigInteger.ONE);
        return inError;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            ConfigurationType configuration = Deserializer.fromXml(inputDirectory, "configuration-anonymized.xml", ConfigurationType.class);

            // vote
            VoteType vote = configuration.getContest().getVoteInformation().getVote();
            String voteIdentification = configuration.getContest().getVoteInformation().getVote().getVoteIdentification();
            List<BallotType> ballots = vote.getBallot();
            int numberOfQuestions = 0;
            for(BallotType ballot : ballots){
                VariantBallotType variantBallot = ballot.getVariantBallot();
                StandardBallotType standardBallot = ballot.getStandardBallot();
                if( variantBallot != null ){
                    List<StandardQuestionType> standardQuestions = variantBallot.getStandardQuestion();
                    for(StandardQuestionType question : standardQuestions){
                        List<JAXBElement<?>> questionIdentificationOrQuestionPositionOrAnswerType = question.getQuestionIdentificationOrQuestionPositionOrAnswerType();
                        List<JAXBElement<AnswerType>> listAnswerType = (List<JAXBElement<AnswerType>>)(List<?>) questionIdentificationOrQuestionPositionOrAnswerType;;
                        numberOfQuestions += listAnswerType.size();
                    }
                    numberOfQuestions += variantBallot.getTieBreakQuestion().getAnswer().size();
                }
                if( standardBallot != null ){
                    numberOfQuestions += standardBallot.getAnswer().size();
                }
            }

            //election
            HashMap<ElectionType, HashMap<String, Integer>> electionsMap = new HashMap<>();

            List<ElectionInformationType> elections = configuration.getContest().getElectionInformation();
            for(ElectionInformationType electionInfo : elections){
                int numberOfMandates = Integer.parseInt(electionInfo.getElection().getNumberOfMandates());
                int writeInsAllowed = electionInfo.getElection().getWriteInsAllowed().equals("true") ? 1 : 0 ;
                int candidateAccumulation = Integer.parseInt(electionInfo.getElection().getCandidateAccumulation());
                List<CandidateType> candidates = electionInfo.getCandidate();
                int candidateVotingOption = ( candidates.size() * candidateAccumulation ) + numberOfMandates * ( 1 + writeInsAllowed );
                List<ListType> listes = electionInfo.getList();

                HashMap<String, Integer> electionDetails = new HashMap();
                electionDetails.put("candidateVotingOption", candidateVotingOption);
                electionDetails.put("candidates", candidates.size());
                electionDetails.put("listes", listes.size());
                electionsMap.put(electionInfo.getElection(),electionDetails );
            }


            DataConfigEE dataConfigEE = Deserializer.fromJson(inputDirectory, "dataConfig_[EE].json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            //votations
            Collection<Integer> temp = ballotBoxes.stream()
                    .flatMap(bb -> bb.getCountingCircles().stream())
                    .flatMap(cc -> cc.getDomainOfInfluence().stream())
                    .flatMap(doi -> doi.getVotes().stream())
                    .flatMap(v -> v.getQuestions().stream())
                    .flatMap(q -> q.getOptions().stream())
                    .map(Option::getPrimeNumber)
                    .collect(Collectors.toList());

            String pString = "1a";
            BigInteger p = TypeConverter.base64ToBigInteger(pString);

            FileInputStream fis = new FileInputStream(new File(inputDirectory + "/commitmentParameters.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            List<BigInteger> numbers = new ArrayList<>();
            String line = null;
            while ((line = br.readLine()) != null) {
                numbers.add(new BigInteger(line));
            }
            br.close();

            if (numbers.isEmpty()) {
                throw new Exception("No such numbers was found in commitmentParameters file");
            } else {
                List<BigInteger> errors = numbers.stream()
                        .filter(bigInteger -> isBigIntInError(bigInteger, p))
                        .collect(Collectors.toList());
                if (errors.isEmpty()) {
                    result.setStatus(Status.OK);
                } else {
                    result.setStatus(Status.NOK);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.nok.message", errors.toString()));
                }
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
