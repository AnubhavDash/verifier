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

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class BallotBox {

	private final UUID id;
	private final String alias;
	private final UUID authId;
	private final UUID vcsId;
	private final String vcsAlias;
	private final int votingCardsGenerated;
	private final List<CountingCircle> countingCircles;

	public BallotBox(
			@JsonProperty("id")
					UUID id,
			@JsonProperty("alias")
					String alias,
			@JsonProperty("authId")
					UUID authId,
			@JsonProperty("vcsId")
					UUID vcsId,
			@JsonProperty("vcsAlias")
					String vcsAlias,
			@JsonProperty("votingCardsGenerated")
					int votingCardsGenerated,
			@JsonProperty("countingCircles")
					CountingCircle[] countingCircles) {
		this.id = id;
		this.alias = alias;
		this.authId = authId;
		this.vcsId = vcsId;
		this.vcsAlias = vcsAlias;
		this.votingCardsGenerated = votingCardsGenerated;
		this.countingCircles = ImmutableList.copyOf(countingCircles);
	}
}
