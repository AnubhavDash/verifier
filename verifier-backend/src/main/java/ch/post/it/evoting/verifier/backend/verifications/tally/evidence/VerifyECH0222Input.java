/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static com.google.common.base.Preconditions.checkNotNull;

import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

/**
 * Regroups the input values needed by the VerifyECH0222 algorithm.
 *
 * <ul>
 *     <li>Election Event Configuration, the configuration-anonymized as {@link Configuration}. Not null.</li>
 *     <li>Tally Control Component Detailed Results, the eCH-0222 as {@link Delivery}. Not null.</li>
 *     <li>Map<sub>decodedVotes</sub>, the key-value map of L<sub>decodedVotes</sub> per authorization name. Not null.</li>
 *     <li>Map<sub>writeIns</sub>, the key-value map of L<sub>writeIns</sub> per authorization name. Not null.</li>
 * </ul>
 */
public class VerifyECH0222Input {

	private final Configuration cantonConfig;
	private final Delivery tallyComponentEch0222;
	private final ImmutableMap<String, TallyComponentVotesPayload> tallyComponentVotesPayloads;

	private VerifyECH0222Input(
			final Configuration cantonConfig,
			final Delivery tallyComponentEch0222,
			final ImmutableMap<String, TallyComponentVotesPayload> tallyComponentVotesPayloads) {
		this.cantonConfig = cantonConfig;
		this.tallyComponentEch0222 = tallyComponentEch0222;
		this.tallyComponentVotesPayloads = tallyComponentVotesPayloads;
	}

	public Configuration getCantonConfig() {
		return cantonConfig;
	}

	public Delivery getTallyComponentEch0222() {
		return tallyComponentEch0222;
	}

	public final ImmutableMap<String, TallyComponentVotesPayload> getTallyControlComponentVotesPerAuthorizationName() {
		return tallyComponentVotesPayloads;
	}

	public static class Builder {

		private Configuration cantonConfig;
		private Delivery tallyComponentEch0222;
		private ImmutableMap<String, TallyComponentVotesPayload> tallyComponentVotesPayloads;

		public Builder setCantonConfig(final Configuration cantonConfig) {
			this.cantonConfig = cantonConfig;
			return this;
		}

		public Builder setTallyComponentEch0222(final Delivery tallyComponentEch0222) {
			this.tallyComponentEch0222 = tallyComponentEch0222;
			return this;
		}

		public Builder setTallyControlComponentVotesPerAuthorizationName(final ImmutableMap<String, TallyComponentVotesPayload> tallyComponentVotesPayloads) {
			this.tallyComponentVotesPayloads = tallyComponentVotesPayloads;
			return this;
		}

		public VerifyECH0222Input build() {
			checkNotNull(cantonConfig);
			checkNotNull(tallyComponentEch0222);
			checkNotNull(tallyComponentVotesPayloads);

			return new VerifyECH0222Input(cantonConfig, tallyComponentEch0222,
					tallyComponentVotesPayloads);
		}
	}

}
