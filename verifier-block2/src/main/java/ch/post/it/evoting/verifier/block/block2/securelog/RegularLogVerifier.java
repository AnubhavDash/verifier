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
package ch.post.it.evoting.verifier.block.block2.securelog;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Arrays;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.ConversionService;

@Service
public class RegularLogVerifier {

	/**
	 * Verifies the HMAC for a regular log entry.
	 *
	 * @param input the inputs needed for the verification. Not null.
	 * @return true if the verification succeeds, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyLogHmac(final VerifyLogHmacInput input) {
		checkNotNull(input);
		final var k = input.currentSessionKey;
		final var h_minus_one = input.previousHmac;
		final var t = input.timestamp;
		final var m = input.logMessage;
		final var h = input.loggedHmac;

		checkArgument(k.length == 32);
		checkArgument(h_minus_one.length == 32);
		checkArgument(t.compareTo(BigInteger.ZERO) >= 0);
		checkArgument(!m.contains("\n") && !m.contains("\r"), "Invalid message, contains CR or LF character");
		checkArgument(h.length == 32);

		final var t_b = ConversionService.integerToByteArray(t);
		final var m_b = ConversionService.stringToByteArray(m);

		final var hMac = new HMac(new SHA256Digest());
		final var h_prime = new byte[hMac.getMacSize()];
		var keyParameter = new KeyParameter(k);
		hMac.init(keyParameter);
		hMac.update(h_minus_one, 0, h_minus_one.length);
		hMac.update(t_b, 0, t_b.length);
		hMac.update(m_b, 0, m_b.length);
		hMac.doFinal(h_prime, 0);

		return Arrays.equals(h, h_prime);
	}

	public static final class VerifyLogHmacInput {
		private final byte[] currentSessionKey;
		private final byte[] previousHmac;
		private final BigInteger timestamp;
		private final String logMessage;
		private final byte[] loggedHmac;

		/**
		 * Constructs a VerifyLogHmacInput. All inputs must be non null.
		 **/
		private VerifyLogHmacInput(final byte[] currentSessionKey, final byte[] previousHmac, final BigInteger timestamp, final String logMessage,
				final byte[] loggedHmac) {
			this.currentSessionKey = checkNotNull(currentSessionKey);
			this.previousHmac = checkNotNull(previousHmac);
			this.timestamp = checkNotNull(timestamp);
			this.logMessage = checkNotNull(logMessage);
			this.loggedHmac = checkNotNull(loggedHmac);
		}

		static VerifyLogHmacInputBuilder builder() {
			return new VerifyLogHmacInputBuilder();
		}

		public static class VerifyLogHmacInputBuilder {
			private byte[] currentSessionKey;
			private byte[] previousHmac;
			private BigInteger timestamp;
			private String logMessage;
			private byte[] loggedHmac;

			private VerifyLogHmacInputBuilder() {}

			public VerifyLogHmacInputBuilder withCurrentSessionKey(byte[] currentSessionKey) {
				this.currentSessionKey = checkNotNull(currentSessionKey);
				return this;
			}

			public VerifyLogHmacInputBuilder withPreviousHmac(byte[] previousHmac) {
				this.previousHmac = checkNotNull(previousHmac);
				return this;
			}

			public VerifyLogHmacInputBuilder withTimestamp(BigInteger timestamp) {
				this.timestamp = checkNotNull(timestamp);
				return this;
			}

			public VerifyLogHmacInputBuilder withLogMessage(String logMessage) {
				this.logMessage = checkNotNull(logMessage);
				return this;
			}

			public VerifyLogHmacInputBuilder withLoggedHmac(byte[] loggedHmac) {
				this.loggedHmac = checkNotNull(loggedHmac);
				return this;
			}

			public VerifyLogHmacInput build() {
				checkNotNull(currentSessionKey);
				checkNotNull(previousHmac);
				checkNotNull(timestamp);
				checkNotNull(logMessage);
				checkNotNull(loggedHmac);
				return new VerifyLogHmacInput(currentSessionKey, previousHmac, timestamp, logMessage, loggedHmac);
			}
		}
	}
}
