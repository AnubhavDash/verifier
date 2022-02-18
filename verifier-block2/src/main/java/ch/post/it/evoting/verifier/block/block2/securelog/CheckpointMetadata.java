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

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class CheckpointMetadata implements LogLineMetadata {

	@JsonProperty(value = "HMAC", required = true)
	private byte[] hmac;

	@JsonProperty(value = "TS", required = true)
	private BigInteger timestamp;

	@JsonProperty(value = "ESK", required = true)
	private byte[] encryptedSessionKey;

	@JsonProperty(value = "LS", required = true)
	private BigInteger maxLines;

	@JsonProperty(value = "SG", required = true)
	private byte[] signature;

	@JsonProperty(value = "LSK")
	private byte[] liberatedSessionKey;

	@JsonProperty(value = "PHMAC")
	private byte[] previousHmac;

	static List<String> requiredFields() {
		return Arrays.asList("HMAC", "TS", "ESK", "LS", "SG");
	}

	@JsonCreator
	CheckpointMetadata(
			@JsonProperty(value = "LSK")
					byte[] liberatedSessionKey,
			@JsonProperty(value = "ESK", required = true)
					byte[] encryptedSessionKey,
			@JsonProperty(value = "PHMAC")
					byte[] previousHmac,
			@JsonProperty(value = "LS", required = true)
					BigInteger maxLines,
			@JsonProperty(value = "TS", required = true)
					BigInteger timestamp,
			@JsonProperty(value = "HMAC", required = true)
					byte[] hmac,
			@JsonProperty(value = "SG", required = true)
					byte[] signature) {
		this.hmac = checkNotNull(hmac);
		this.timestamp = checkNotNull(timestamp);
		this.encryptedSessionKey = checkNotNull(encryptedSessionKey);
		this.maxLines = checkNotNull(maxLines);
		this.signature = checkNotNull(signature);
		this.liberatedSessionKey = liberatedSessionKey == null ? new byte[]{} : liberatedSessionKey;
		this.previousHmac = previousHmac == null ? new byte[] {} : previousHmac;
	}

	@Override
	public String toString() {
		return "CheckpointMetadata{" +
				"hmac=" + Arrays.toString(hmac) +
				", timestamp=" + timestamp +
				", encryptedSessionKey=" + Arrays.toString(encryptedSessionKey) +
				", maxLines=" + maxLines +
				", signature=" + Arrays.toString(signature) +
				", liberatedSessionKey=" + Arrays.toString(liberatedSessionKey) +
				", previousHmac=" + Arrays.toString(previousHmac) +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CheckpointMetadata metadata = (CheckpointMetadata) o;
		return Arrays.equals(hmac, metadata.hmac) && Objects.equals(timestamp, metadata.timestamp) && Arrays.equals(
				encryptedSessionKey, metadata.encryptedSessionKey) && Objects.equals(maxLines, metadata.maxLines) && Arrays.equals(
				signature, metadata.signature) && Arrays.equals(liberatedSessionKey, metadata.liberatedSessionKey) && Arrays.equals(
				previousHmac, metadata.previousHmac);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(timestamp, maxLines);
		result = 31 * result + Arrays.hashCode(hmac);
		result = 31 * result + Arrays.hashCode(encryptedSessionKey);
		result = 31 * result + Arrays.hashCode(signature);
		result = 31 * result + Arrays.hashCode(liberatedSessionKey);
		result = 31 * result + Arrays.hashCode(previousHmac);
		return result;
	}

	public byte[] getHmac() {
		return hmac;
	}

	public BigInteger getTimestamp() {
		return timestamp;
	}

	public byte[] getEncryptedSessionKey() {
		return encryptedSessionKey;
	}

	public BigInteger getMaxLines() {
		return maxLines;
	}

	public byte[] getSignature() {
		return signature;
	}

	public byte[] getLiberatedSessionKey() {
		return liberatedSessionKey;
	}

	public byte[] getPreviousHmac() {
		return previousHmac;
	}
}
