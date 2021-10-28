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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class Ballot {

	private final CastVote vote;
	private final Receipt receipt;
	private final AuthenticationToken authenticationToken;

	public Ballot(
			@JsonProperty("vote")
					CastVote vote,
			@JsonProperty("receipt")
					Receipt receipt,
			@JsonProperty("authenticationToken")
					AuthenticationToken authenticationToken) {
		this.vote = vote;
		this.receipt = receipt;
		this.authenticationToken = authenticationToken;
	}
}
