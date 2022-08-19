package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity.verifications;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.evoting.cryptoprimitives.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureGeneration;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.ChannelSecurityContextData;
import ch.post.it.evoting.verifier.protocol.domain.configuration.EncryptionParametersPayload;

class CheckSignatureEncryptionParametersTest extends SetupVerificationTest {

	private ElectionDataExtractionService electionDataExtractionService;

	@BeforeEach
	void setUpAll() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);
		final SignatureVerification testSignatureVerification = signatureFactory.getTestSignatureVerification();
		verification = new CheckSignatureEncryptionParameters(applicationEventPublisherMock, electionDataExtractionService,
				testSignatureVerification);
	}

	@Test
	void testAbsentFileDoesNotVerify() {
		assertThrows(Exception.class, () -> verification.verify(Path.of("")));
	}

	@Test
	void testExpectedSignerSuccess() throws SignatureException {
		final EncryptionParametersPayload encryptionParametersPayload = electionDataExtractionService.getEncryptionParametersPayload(datasetPath);
		final SignatureGeneration testSignatureGeneration = signatureFactory.getTestSignatureGeneration(Alias.SDM_CONFIG);
		final byte[] signature = testSignatureGeneration.genSignature(encryptionParametersPayload,
				ChannelSecurityContextData.setupComponentEncryptionParameters());
		encryptionParametersPayload.setSignature(new CryptoPrimitivesSignature(signature));
		assertTrue(((CheckSignatureEncryptionParameters) verification).verifySignature(encryptionParametersPayload));
	}

	@Test
	void testUnexpectedSignerFails() throws SignatureException {
		final EncryptionParametersPayload encryptionParametersPayload = electionDataExtractionService.getEncryptionParametersPayload(datasetPath);
		final SignatureGeneration testSignatureGeneration = signatureFactory.getTestSignatureGeneration(Alias.CONTROL_COMPONENT_1);
		final byte[] wrongSignature = testSignatureGeneration.genSignature(encryptionParametersPayload,
				ChannelSecurityContextData.setupComponentEncryptionParameters());
		encryptionParametersPayload.setSignature(new CryptoPrimitivesSignature(wrongSignature));
		assertFalse(((CheckSignatureEncryptionParameters) verification).verifySignature(encryptionParametersPayload));
	}
}