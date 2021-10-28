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
package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class Metadata {

	private final String version;
	private final List<SignedItem> signedItems;
	private final String algorithm;
	private final String base64Signature;

	public Metadata(
			@JsonProperty("version")
					String version,
			@JsonProperty("signed")
					SignedItem[] signedItems,
			@JsonProperty("alg")
					String algorithm,
			@JsonProperty("signature")
					String base64Signature) {
		this.version = version;
		this.signedItems = ImmutableList.copyOf(signedItems);
		this.algorithm = algorithm;
		this.base64Signature = base64Signature;
	}

	public byte[] getSignature() {
		return Base64.getDecoder().decode(base64Signature);
	}
}
