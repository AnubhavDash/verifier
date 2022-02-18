/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block2.securelog;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.ConversionService;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationException;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;

@Service
public class CheckpointVerifier {

	/**
	 * Verifies the signature for the checkpoint log entry.
	 *
	 * @param input inputs needed for the verification. Not null.
	 * @return true if the verification succeeds, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyCheckpointSignature(final VerifyCheckpointSignatureInput input) {
		checkNotNull(input);

		final var k_sig = checkNotNull(input.signingPublicKey);
		final var h_minus_one = checkNotNull(input.previousHmac);
		final var k_l = checkNotNull(input.liberatedSessionKey);
		final var k_e = checkNotNull(input.encryptedSessionKey);
		final var n = checkNotNull(input.maxLineNumber);
		final var t = checkNotNull(input.timestamp);
		final var m = checkNotNull(input.logMessage);
		final var h = checkNotNull(input.hmac);
		final var s = checkNotNull(input.checkpointSignature);

		checkArgument(h_minus_one.length == 0 && k_l.length == 0 || h_minus_one.length == 32 && k_l.length == 32,
				"Liberated session key and previous hmac must be either both empty or both of length 32 bytes");
		checkArgument(n.compareTo(BigInteger.ZERO) > 0);
		checkArgument(t.compareTo(BigInteger.ZERO) > 0);
		checkArgument(!m.contains("\n") && !m.contains("\r"), "Invalid message, contains CR or LF character");
		checkArgument(h.length == 32);

		final var n_b = ConversionService.integerToByteArray(n);
		final var t_b = ConversionService.integerToByteArray(t);
		final var m_b = ConversionService.stringToByteArray(m);

		try {
			var signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(k_sig);
			signature.update(h_minus_one);
			signature.update(k_l);
			signature.update(k_e);
			signature.update(n_b);
			signature.update(t_b);
			signature.update(m_b);
			signature.update(h);
			return signature.verify(s);

		} catch (NoSuchAlgorithmException e) {
			throw new VerificationPreconditionException("Cannot find signing algorithm SHA256withRSA", e);
		} catch (InvalidKeyException e) {
			throw new VerificationException("Signing key is invalid", e);
		} catch (SignatureException e) {
			throw new VerificationException("Problems verifying the signature", e);
		}
	}

	/**
	 * Verifies the HMAC for a checkpoint log entry.
	 *
	 * @param input inputs needed for the verification. Not null.
	 * @return boolean if the verification
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyLogHmacCheckpoint(VerifyLogHmacCheckpointInput input) {
		checkNotNull(input);

		final var k = checkNotNull(input.currentSessionKey);
		final var h_minus_one = checkNotNull(input.previousHmac);
		final var k_l = checkNotNull(input.liberatedSessionKey);
		final var k_e = checkNotNull(input.encryptedSessionKey);
		final var n = checkNotNull(input.maxLineNumber);
		final var t = checkNotNull(input.timestamp);
		final var m = checkNotNull(input.logMessage);
		final var h = checkNotNull(input.hmac);

		checkArgument(k.length == 32, "Invalid current session key length");
		checkArgument(h_minus_one.length == 0 && k_l.length == 0 || h_minus_one.length == 32 && k_l.length == 32,
				"Liberated session key and previous hmac must be either both empty or both of length 32 bytes");
		checkArgument(n.compareTo(BigInteger.ZERO) > 0);
		checkArgument(t.compareTo(BigInteger.ZERO) > 0);
		checkArgument(!m.contains("\n") && !m.contains("\r"), "Invalid message, contains CR or LF character");
		checkArgument(h.length == 32, "Invalid HMAC length");

		final var n_b = ConversionService.integerToByteArray(n);
		final var t_b = ConversionService.integerToByteArray(t);
		final var m_b = ConversionService.stringToByteArray(m);

		final var hMac = new HMac(new SHA256Digest());
		final var h_prime = new byte[hMac.getMacSize()];
		final KeyParameter keyParameter = new KeyParameter(k);
		hMac.init(keyParameter);
		hMac.update(h_minus_one, 0, h_minus_one.length);
		hMac.update(k_l, 0, k_l.length);
		hMac.update(k_e, 0, k_e.length);
		hMac.update(n_b, 0, n_b.length);
		hMac.update(t_b, 0, t_b.length);
		hMac.update(m_b, 0, m_b.length);
		hMac.doFinal(h_prime, 0);

		return Arrays.equals(h, h_prime);
	}

	public static class VerifyCheckpointSignatureInput {
		private final PublicKey signingPublicKey;
		private final byte[] previousHmac;
		private final byte[] liberatedSessionKey;
		private final byte[] encryptedSessionKey;
		private final BigInteger maxLineNumber;
		private final BigInteger timestamp;
		private final String logMessage;
		private final byte[] hmac;
		private final byte[] checkpointSignature;

		private VerifyCheckpointSignatureInput(PublicKey signingPublicKey, byte[] previousHmac, byte[] liberatedSessionKey, byte[] encryptedSessionKey,
				BigInteger maxLineNumber, BigInteger timestamp, String logMessage, byte[] hmac, byte[] checkpointSignature) {
			this.signingPublicKey = checkNotNull(signingPublicKey);
			this.previousHmac = checkNotNull(previousHmac);
			this.liberatedSessionKey = checkNotNull(liberatedSessionKey);
			this.encryptedSessionKey = checkNotNull(encryptedSessionKey);
			this.maxLineNumber = checkNotNull(maxLineNumber);
			this.timestamp = checkNotNull(timestamp);
			this.logMessage = checkNotNull(logMessage);
			this.hmac = checkNotNull(hmac);
			this.checkpointSignature = checkNotNull(checkpointSignature);
		}

		public static VerifyCheckpointSignatureInputBuilder builder() {
			return new VerifyCheckpointSignatureInputBuilder();
		}

		public static class VerifyCheckpointSignatureInputBuilder {
			private PublicKey signingPublicKey;
			private byte[] previousHmac;
			private byte[] liberatedSessionKey;
			private byte[] encryptedSessionKey;
			private BigInteger maxLineNumber;
			private BigInteger timestamp;
			private String logMessage;
			private byte[] hmac;
			private byte[] checkpointSignature;

			public VerifyCheckpointSignatureInputBuilder withSigningPublicKey(final PublicKey signingPublicKey) {
				this.signingPublicKey = checkNotNull(signingPublicKey);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withPreviousHmac(final byte[] previousHmac) {
				this.previousHmac = checkNotNull(previousHmac);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withLiberateSessionKey(final byte[] liberatedSessionKey) {
				this.liberatedSessionKey = checkNotNull(liberatedSessionKey);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withEncryptedSessionKey(final byte[] encryptedSessionKey) {
				this.encryptedSessionKey = checkNotNull(encryptedSessionKey);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withMaxLineNumber(final BigInteger maxLineNumber) {
				this.maxLineNumber = checkNotNull(maxLineNumber);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withTimestamp(final BigInteger timestamp) {
				this.timestamp = checkNotNull(timestamp);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withLogMessage(final String logMessage ) {
				this.logMessage = checkNotNull(logMessage);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withHmac(final byte[] hmac) {
				this.hmac = checkNotNull(hmac);
				return this;
			}

			public VerifyCheckpointSignatureInputBuilder withSignature(final byte[] checkpointSignature) {
				this.checkpointSignature = checkNotNull(checkpointSignature);
				return this;
			}

			public VerifyCheckpointSignatureInput build() {
				return new VerifyCheckpointSignatureInput(signingPublicKey, previousHmac, liberatedSessionKey, encryptedSessionKey, maxLineNumber,
						timestamp, logMessage, hmac, checkpointSignature);
			}
		}
	}

	public static class VerifyLogHmacCheckpointInput {
		private final byte[] currentSessionKey;
		private final byte[] previousHmac;
		private final byte[] liberatedSessionKey;
		private final byte[] encryptedSessionKey;
		private final BigInteger maxLineNumber;
		private final BigInteger timestamp;
		private final String logMessage;
		private final byte[] hmac;

		private VerifyLogHmacCheckpointInput(byte[] currentSessionKey, byte[] previousHmac, byte[] liberatedSessionKey, byte[] encryptedSessionKey,
				BigInteger maxLineNumber, BigInteger timestamp, String logMessage, byte[] hmac) {
			this.currentSessionKey = checkNotNull(currentSessionKey);
			this.previousHmac = checkNotNull(previousHmac);
			this.liberatedSessionKey = checkNotNull(liberatedSessionKey);
			this.encryptedSessionKey = checkNotNull(encryptedSessionKey);
			this.maxLineNumber = checkNotNull( maxLineNumber);
			this.timestamp = checkNotNull(timestamp);
			this.logMessage = checkNotNull(logMessage);
			this.hmac = checkNotNull(hmac);
		}

		public static VerifyLogHmacCheckpointInputBuilder builder() {
			return new VerifyLogHmacCheckpointInputBuilder();
		}

		public static class VerifyLogHmacCheckpointInputBuilder {
			private byte[] currentSessionKey;
			private byte[] previousHmac;
			private byte[] liberatedSessionKey;
			private byte[] encryptedSessionKey;
			private BigInteger maxLineNumber;
			private BigInteger timestamp;
			private String logMessage;
			private byte[] hmac;

			public VerifyLogHmacCheckpointInputBuilder withCurrentSessionKey(final byte[] currentSessionKey) {
				this.currentSessionKey = checkNotNull(currentSessionKey);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withPreviousHmac(final byte[] previousHmac) {
				this.previousHmac = checkNotNull(previousHmac);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withLiberateSessionKey(final byte[] liberatedSessionKey) {
				this.liberatedSessionKey = checkNotNull(liberatedSessionKey);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withEncryptedSessionKey(final byte[] encryptedSessionKey) {
				this.encryptedSessionKey = checkNotNull(encryptedSessionKey);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withMaxLineNumber(final BigInteger maxLineNumber) {
				this.maxLineNumber = checkNotNull(maxLineNumber);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withTimestamp(final BigInteger timestamp) {
				this.timestamp = checkNotNull(timestamp);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withLogMessage(final String logMessage ) {
				this.logMessage = checkNotNull(logMessage);
				return this;
			}

			public VerifyLogHmacCheckpointInputBuilder withHmac(final byte[] hmac) {
				this.hmac = checkNotNull(hmac);
				return this;
			}

			public VerifyLogHmacCheckpointInput build() {
				return new VerifyLogHmacCheckpointInput(currentSessionKey, previousHmac, liberatedSessionKey, encryptedSessionKey, maxLineNumber,
						timestamp, logMessage, hmac);
			}
		}
	}
}
