package ch.post.it.evoting.verifier.block.block2.verifications;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import ch.post.it.evoting.verifier.dto.Vote__1;

public class CheckVoteSignature extends AbstractVerification {

	static final String CREDENTIALS_CA = "credentialsCA";
	static final String ELECTION_ROOT_CA = "electionRootCA";

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification73.description"));
		def.setId(73);
		def.setName("checkVoteSignature");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Mapper to parse json files containing the certificates.
		ObjectMapper mapper = new ObjectMapper();

		// Build election node where certificates are.
		final PathNode electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
		final JsonNode electionInfoNode = mapper.readTree(Files.readAllBytes(electionInfoPathNode.getPath()));

		// Get the intermediate certificates.
		final byte[][] intermediateCertificates = extractIntermediateCertificates(electionInfoNode);

		// Get the root certificate.
		final byte[] rootCertificate = extractRootCertificate(electionInfoNode);

		// Get all the ballot box id directories and iterate over them.
		final PathNode ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path regexPath : ballotIdsPathNode.getRegexPaths()) {
			// Get the downloadedBallotBox file path.
			final PathNode downloadedBallotPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, regexPath);

			try (final Stream<String> lines = Files.lines(downloadedBallotPathNode.getPath())) {
				lines.parallel()
						.map(this::deserializeDownloadedBallot)
						// Remove empty lines, signature etc...
						.filter(Objects::nonNull)
						.forEach(ballot -> {
							// Extract information and its signature.
							final byte[] signature =
									Base64.getDecoder().decode(ballot.getVote().getSignature().getBytes(StandardCharsets.UTF_8));
							final byte[] signedInformation = buildSignedInformation(ballot.getVote());

							// Extract the certificate used to sign.
							final byte[] signingCertificate = ballot.getVote().getCertificate().getBytes(StandardCharsets.UTF_8);

							// Check signature.
							if (!SignatureChecker.verifySignature(signedInformation, signature, signingCertificate,
									intermediateCertificates, rootCertificate)) {
								throw buildVerificationFailureException(
										"The signature verification of the vote failed",
										Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
										"verification73.nok.message",
										ballot.getVote().getVotingCardId()
								);
							}
						});
			}
		}

		result.setStatus(Status.OK);
		return result;
	}

	private byte[][] extractIntermediateCertificates(JsonNode electionInfoNode) {
		final JsonNode servicesCANode = electionInfoNode.path(CREDENTIALS_CA);
		if (servicesCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", CREDENTIALS_CA));
		}
		return new byte[][] { servicesCANode.asText().getBytes(StandardCharsets.UTF_8) };
	}

	private byte[] extractRootCertificate(JsonNode electionInfoNode) {
		final JsonNode electionRootCANode = electionInfoNode.path(ELECTION_ROOT_CA);
		if (electionRootCANode.isMissingNode()) {
			throw new JsonMissingNodeException(String.format("%s certificate is missing!", ELECTION_ROOT_CA));
		}
		return electionRootCANode.asText().getBytes(StandardCharsets.UTF_8);
	}

	private byte[] buildSignedInformation(Vote__1 vote) {
		String concatenatedInfo = Stream.of(
				vote.getEncryptedOptions(),
				vote.getEncryptedWriteIns(),
				vote.getCorrectnessIds(),
				vote.getVerificationCardPKSignature(),
				vote.getAuthenticationTokenSignature(),
				vote.getSchnorrProof(),
				vote.getVotingCardId(),
				vote.getElectionEventId()
		).filter(s -> Objects.nonNull(s) && !s.isEmpty()).collect(Collectors.joining());

		return concatenatedInfo.getBytes(StandardCharsets.UTF_8);
	}

	private DownloadedBallot deserializeDownloadedBallot(String line) {
		if (!line.isEmpty() && line.contains("}}|")) {
			int endJsonObjectIndex = line.indexOf("}}|") + 2;
			String json = line.substring(0, endJsonObjectIndex);
			try {
				return Deserializer.fromJson(TypeConverter.stringToByte(json), DownloadedBallot.class);
			} catch (IOException e) {
				throw new RuntimeException();
			}
		} else {
			return null;
		}
	}

}
