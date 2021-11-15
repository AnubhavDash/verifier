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

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RegularLogLineMetadata implements LogLineMetadata {

	@JsonProperty(value = "HMAC", required = true)
	private byte[] hmac;

	@JsonProperty(value = "TS", required = true)
	private BigInteger timestamp;

	@JsonProperty(value = "LC")
	private BigInteger lineCount;

	static List<String> requiredFields() {
		return Arrays.asList("HMAC", "TS");
	}

	@JsonCreator
	public RegularLogLineMetadata(
			@JsonProperty(value = "HMAC", required = true) byte[] hmac,
			@JsonProperty(value = "TS", required = true) BigInteger timestamp,
			@JsonProperty(value = "LC", required = true) BigInteger lineCount) {
		this.hmac = checkNotNull(hmac);
		this.timestamp = checkNotNull(timestamp);
		this.lineCount = checkNotNull(lineCount);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RegularLogLineMetadata metadata = (RegularLogLineMetadata) o;
		return Arrays.equals(hmac, metadata.hmac) && Objects.equals(timestamp, metadata.timestamp) && Objects.equals(
				lineCount, metadata.lineCount);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(timestamp, lineCount);
		result = 31 * result + Arrays.hashCode(hmac);
		return result;
	}

	@Override
	public String toString() {
		return "RegularLogLineMetadata{" +
				"hmac=" + Arrays.toString(hmac) +
				", timestamp=" + timestamp +
				", lineCount=" + lineCount +
				'}';
	}

	public byte[] getHmac() {
		return hmac;
	}

	public BigInteger getTimestamp() {
		return timestamp;
	}

	public BigInteger getLineCount() {
		return lineCount;
	}
}
