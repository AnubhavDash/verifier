/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block4.verifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.post.it.evoting.cryptoprimitives.GroupVector;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetService;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.test.tools.generator.ElGamalGenerator;
import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.verifier.block.block4.verifications.VerifyOfflineShuffleProof.VerifyOfflineShuffleProofInput;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;

class VerifyOfflineShuffleProofTest extends Block4VerificationAbstractTest {

	private static final ElectionDataExtractionService EXTRACTION_SERVICE = new ElectionDataExtractionService(new PathService(),
			DomainObjectMapper.getNewInstance());
	private static final MixnetService MIXNET_SERVICE = new MixnetService();
	private static final VerifyOfflineShuffleProof VERIFY = new VerifyOfflineShuffleProof(EXTRACTION_SERVICE, MIXNET_SERVICE);
	private static final SecureRandom RANDOM = new SecureRandom();

	private static final GqGroup gqGroup = new GqGroup(new BigInteger(
			"22588801568735561413035633152679913053449200833478689904902877673687016391844561133376032309307885537704777240609087377993341380751697605235541131273868440070920362148431866829787784445019147999379498503693247429579480289226602748397335327890884464685051682703709742724121783217827040722415360103179289160056581759372475845985438977307323570530753362027145384124771826114651710264766437273044759690955051982839684910462609395741692689616014805965573558015387956017183286848440036954926101719205598449898400180082053755864070690174202432196678045052744337832802051787273056312757384654145455745603262082348042780103679"),
			new BigInteger(
					"11294400784367780706517816576339956526724600416739344952451438836843508195922280566688016154653942768852388620304543688996670690375848802617770565636934220035460181074215933414893892222509573999689749251846623714789740144613301374198667663945442232342525841351854871362060891608913520361207680051589644580028290879686237922992719488653661785265376681013572692062385913057325855132383218636522379845477525991419842455231304697870846344808007402982786779007693978008591643424220018477463050859602799224949200090041026877932035345087101216098339022526372168916401025893636528156378692327072727872801631041174021390051839"),
			BigInteger.valueOf(2));

	private static final GqGroup otherGqGroup = new GqGroup(new BigInteger(
			"B7E151628AED2A6ABF7158809CF4F3C762E7160F38B4DA56A784D9045190CFEF324E7738926CFBE5F4BF8D8D8C31D763DA06C80ABB1185EB4F7C7B5757F5958490CFD47D7C19BB42158D9554F7B46BCED55C4D79FD5F24D6613C31C3839A2DDF8A9A276BCFBFA1C877C56284DAB79CD4C2B3293D20E9E5EAF02AC60ACC93ED874422A52ECB238FEEE5AB6ADD835FD1A0753D0A8F78E537D2B95BB79D8DCAEC642C1E9F23B829B5C2780BF38737DF8BB300D01334A0D0BD8645CBFA73A6160FFE393C48CBBBCA060F0FF8EC6D31BEB5CCEED7F2F0BB088017163BC60DF45A0ECB1BCD289B06CBBFEA21AD08E1847F3F7378D56CED94640D6EF0D3D37BE69D0063",
			16), new BigInteger(
			"5BF0A8B1457695355FB8AC404E7A79E3B1738B079C5A6D2B53C26C8228C867F799273B9C49367DF2FA5FC6C6C618EBB1ED0364055D88C2F5A7BE3DABABFACAC24867EA3EBE0CDDA10AC6CAAA7BDA35E76AAE26BCFEAF926B309E18E1C1CD16EFC54D13B5E7DFD0E43BE2B1426D5BCE6A6159949E9074F2F5781563056649F6C3A21152976591C7F772D5B56EC1AFE8D03A9E8547BC729BE95CADDBCEC6E57632160F4F91DC14DAE13C05F9C39BEFC5D98068099A50685EC322E5FD39D30B07FF1C9E2465DDE5030787FC763698DF5AE6776BF9785D84400B8B1DE306FA2D07658DE6944D8365DFF510D68470C23F9FB9BC6AB676CA3206B77869E9BDF34E8031",
			16), new BigInteger(
			"1CB7C6D53960F1ABA5254DD328022F899DA8A86C809CA0CFC474A4BF183D9A79F75289DA2ACC9FF38CB57BD80EC3F24B647033B6524684FF4062732ED1F79467CA02B7A35F615388CCF9DD638A0916D7B90E83F8C3562B8A6DEC66A98847FCD8159682539A9FB8C1ACA7F07209645681123B2AC89DBACA18D1B4D245D44E31E68AF03226DAC36472DAF1E170CFFA0095A06A8427B428FDB03EBB40D241B5AEA9F491CB0AAB1B175464351F22D5D5004747AA483E97770C495B05F227CE46F28317495DFD0D7C789ECCB597BB5B2F357811303697D4B8475F1100C173E50A009811F07F4B0E16C4876D871EEB2C588874C4C422F7DDC79EDD3B276F3BF5E36D9",
			16));

	private static String electionEventId;
	private static String ballotBoxId;
	private static ElGamalGenerator elGamalGenerator;
	private static ElGamalGenerator otherElGamalGenerator;

	private int ciphertextLength;
	private int ciphertextVectorSize;
	private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
	private ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes;
	private ShuffleArgument shuffleProof;

	public VerifyOfflineShuffleProofTest() {
		super(VerifyOfflineShuffleProof.class);
	}

	@BeforeAll
	static void setupAll() {
		electionEventId = "dd4063884c144446a6dfb63c42eb9e86";
		ballotBoxId = "1ec5fabe10bb49b0a16f5aa7fbe632fc";
		elGamalGenerator = new ElGamalGenerator(gqGroup);
		otherElGamalGenerator = new ElGamalGenerator(otherGqGroup);
	}

	@BeforeEach
	void setup() {
		ciphertextLength = RANDOM.nextInt(3) + 1;
		ciphertextVectorSize = RANDOM.nextInt(9) + 2;
		partiallyDecryptedVotes = elGamalGenerator.genRandomCiphertextVector(ciphertextVectorSize, ciphertextLength);
		electoralBoardPublicKey = elGamalGenerator.genRandomPublicKey(ciphertextLength);
		final VerifiableShuffle verifiableShuffle = MIXNET_SERVICE.genVerifiableShuffle(partiallyDecryptedVotes, electoralBoardPublicKey);
		shuffledVotes = verifiableShuffle.getShuffledCiphertexts();
		shuffleProof = verifiableShuffle.getShuffleArgument();
	}

	static Stream<Arguments> getInputsWithNull() {
		final int ciphertextLength = RANDOM.nextInt(3) + 1;
		final int ciphertextVectorSize = RANDOM.nextInt(9) + 2;
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes = elGamalGenerator.genRandomCiphertextVector(
				ciphertextVectorSize, ciphertextLength);
		final ElGamalMultiRecipientPublicKey electoralBoardPublicKey = elGamalGenerator.genRandomPublicKey(ciphertextLength);
		final VerifiableShuffle verifiableShuffle = MIXNET_SERVICE.genVerifiableShuffle(partiallyDecryptedVotes, electoralBoardPublicKey);
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> shuffledVotes = verifiableShuffle.getShuffledCiphertexts();
		final ShuffleArgument shuffleProof = verifiableShuffle.getShuffleArgument();

		return Stream.of(
				Arguments.of(new VerifyOfflineShuffleProofInput(ciphertextLength, null, electoralBoardPublicKey, shuffledVotes, shuffleProof)),
				Arguments.of(new VerifyOfflineShuffleProofInput(ciphertextLength, partiallyDecryptedVotes, null, shuffledVotes, shuffleProof)),
				Arguments.of(
						new VerifyOfflineShuffleProofInput(ciphertextLength, partiallyDecryptedVotes, electoralBoardPublicKey, null, shuffleProof)),
				Arguments.of(
						new VerifyOfflineShuffleProofInput(ciphertextLength, partiallyDecryptedVotes, electoralBoardPublicKey, shuffledVotes, null))
		);
	}

	@ParameterizedTest
	@MethodSource("getInputsWithNull")
	void verifyOfflineShuffleProofBallotBoxWithNullArguments(final VerifyOfflineShuffleProofInput input) {
		assertThrows(NullPointerException.class, () -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
	}

	@Test
	void verifyOfflineShuffleProofWithValidInputs() {
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				partiallyDecryptedVotes, electoralBoardPublicKey, shuffledVotes, shuffleProof);
		final boolean result = assertDoesNotThrow(
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertTrue(result);
	}

	@Test
	void verifyOfflineShuffleProofWithTooLongPartiallyDecryptedVotesVector() {
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> tooLongPartiallyEncryptedVotes = elGamalGenerator.genRandomCiphertextVector(
				ciphertextVectorSize + 1, ciphertextLength);
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				tooLongPartiallyEncryptedVotes, electoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("There must be as many partially decrypted votes as there are shuffled votes.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofWithTooLongPartiallyDecryptedVotes() {
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> tooLongPartiallyEncryptedVotes = elGamalGenerator.genRandomCiphertextVector(
				ciphertextVectorSize, ciphertextLength + 1);
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				tooLongPartiallyEncryptedVotes, electoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The partially decrypted votes and the shuffled votes must have the same element size.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofWithPublicKeyDifferentGroup() {
		final ElGamalMultiRecipientPublicKey otherElectoralBoardPublicKey = otherElGamalGenerator.genRandomPublicKey(ciphertextLength);
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				partiallyDecryptedVotes, otherElectoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The partially decrypted votes and the electoral board public key must have the same group.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofWithShuffledVotesDifferentGroup() {
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> otherShuffledVotes = otherElGamalGenerator.genRandomCiphertextVector(
				ciphertextVectorSize, ciphertextLength);
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				partiallyDecryptedVotes, electoralBoardPublicKey, otherShuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The partially decrypted votes and the shuffled votes must have the same group.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofBallotBoxTooManyAllowedWriteIns() {
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength + 1,
				partiallyDecryptedVotes, electoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The partially decrypted votes must have one more element than the number of allowed write-ins.", exception.getMessage());
	}

	@Test
	void verifyOfflinehuffleProofWithOneVote() {
		partiallyDecryptedVotes = elGamalGenerator.genRandomCiphertextVector(1, ciphertextLength);
		electoralBoardPublicKey = elGamalGenerator.genRandomPublicKey(1);
		shuffledVotes = mock(GroupVector.class);
		when(shuffledVotes.size()).thenReturn(1);
		when(shuffledVotes.getElementSize()).thenReturn(ciphertextLength);
		when(shuffledVotes.getGroup()).thenReturn(gqGroup);

		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				partiallyDecryptedVotes, electoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("There must be at least two votes.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofWithEmptyCiphertexts() {
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> emptyPartiallyDecryptedVotes = spy(partiallyDecryptedVotes);
		when(emptyPartiallyDecryptedVotes.getElementSize()).thenReturn(0);
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> emptyShuffledVotes = spy(shuffledVotes);
		when(emptyShuffledVotes.getElementSize()).thenReturn(0);

		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(0, emptyPartiallyDecryptedVotes,
				electoralBoardPublicKey, emptyShuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The votes must have at least one phi element.", exception.getMessage());
	}

	@Test
	void verifyOfflineShuffleProofBallotBoxWithTooManyPhiElements() {
		ciphertextLength++;
		partiallyDecryptedVotes = elGamalGenerator.genRandomCiphertextVector(ciphertextVectorSize, ciphertextLength);
		electoralBoardPublicKey = elGamalGenerator.genRandomPublicKey(ciphertextLength);
		final VerifiableShuffle verifiableShuffle = MIXNET_SERVICE.genVerifiableShuffle(partiallyDecryptedVotes, electoralBoardPublicKey);
		shuffledVotes = verifiableShuffle.getShuffledCiphertexts();
		shuffleProof = verifiableShuffle.getShuffleArgument();
		final ElGamalMultiRecipientPublicKey tooSmallElectoralBoardPublicKey = elGamalGenerator.genRandomPublicKey(ciphertextLength - 1);
		final VerifyOfflineShuffleProofInput input = new VerifyOfflineShuffleProofInput(ciphertextLength,
				partiallyDecryptedVotes, tooSmallElectoralBoardPublicKey, shuffledVotes, shuffleProof);
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> VERIFY.verifyOfflineShuffleProofBallotBox(input));
		assertEquals("The votes must not have more phi elements than electoral board public key elements.", exception.getMessage());
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(Objects.requireNonNull(getClass().getResource("/VerifyOfflineShuffleProofTest/OK")).toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(Objects.requireNonNull(getClass().getResource("/VerifyOfflineShuffleProofTest/NOK")).toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.NOK, verificationResult.getStatus());
		assertEquals(TranslationHelper.getFromResourceBundle("block4/resources", "verification11.nok.message"), verificationResult.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() throws URISyntaxException {
		final Path path = Paths
				.get(Objects.requireNonNull(getClass().getResource("/VerifyOfflineShuffleProofTest/NOK_missingFiles")).toURI());
		assertThrows(UncheckedIOException.class, () -> verification.verify(path));
	}

	@Test
	void executeTestNOKCorruptedFile() throws URISyntaxException {
		final Path path = Paths
				.get(Objects.requireNonNull(getClass().getResource("/VerifyOfflineShuffleProofTest/NOK_corruptedFile")).toURI());
		UncheckedIOException exception = assertThrows(UncheckedIOException.class, () -> verification.verify(path));
		assertNotNull(exception);
	}
}