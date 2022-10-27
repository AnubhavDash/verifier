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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

public class VerifyTallyFilesInput {

	private final Configuration setupComponentConfig;
	private final Results tallyComponentDecrypt;
	private final Delivery tallyComponentEch0110;
	private final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222;
	private final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads;

	private VerifyTallyFilesInput(
			final Configuration setupComponentConfig,
			final Results tallyComponentDecrypt,
			final Delivery tallyComponentEch0110,
			final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222,
			final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads) {
		this.setupComponentConfig = setupComponentConfig;
		this.tallyComponentDecrypt = tallyComponentDecrypt;
		this.tallyComponentEch0110 = tallyComponentEch0110;
		this.tallyComponentEch0222 = tallyComponentEch0222;
		this.tallyComponentVotesPayloads = tallyComponentVotesPayloads;
	}

	public Configuration getSetupComponentConfig() {
		return setupComponentConfig;
	}

	public Results getTallyComponentDecrypt() {
		return tallyComponentDecrypt;
	}

	public Delivery getTallyComponentEch0110() {
		return tallyComponentEch0110;
	}

	public ch.ech.xmlns.ech_0222._1.Delivery getTallyComponentEch0222() {
		return tallyComponentEch0222;
	}

	public final Map<String, TallyComponentVotesPayload> getTallyComponentVotesPayloads() {
		return tallyComponentVotesPayloads;
	}

	public static class Builder {

		private Configuration setupComponentConfig;
		private Results tallyComponentDecrypt;
		private Delivery tallyComponentEch0110;
		private ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222;
		private Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads;

		public Builder setupComponentConfig(final Configuration setupComponentConfig) {
			this.setupComponentConfig = setupComponentConfig;
			return this;
		}

		public Builder setTallyComponentDecrypt(final Results tallyComponentDecrypt) {
			this.tallyComponentDecrypt = tallyComponentDecrypt;
			return this;
		}

		public Builder setTallyComponentEch0110(final Delivery tallyComponentEch0110) {
			this.tallyComponentEch0110 = tallyComponentEch0110;
			return this;
		}

		public Builder setTallyComponentEch0222(final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222) {
			this.tallyComponentEch0222 = tallyComponentEch0222;
			return this;
		}

		public Builder setTallyComponentVotesPayloads(final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads) {
			this.tallyComponentVotesPayloads = Map.copyOf(tallyComponentVotesPayloads);
			return this;
		}

		public VerifyTallyFilesInput build() {
			checkNotNull(setupComponentConfig);
			checkNotNull(tallyComponentDecrypt);
			checkNotNull(tallyComponentEch0110);
			checkNotNull(tallyComponentEch0222);
			checkNotNull(tallyComponentVotesPayloads);

			return new VerifyTallyFilesInput(setupComponentConfig, tallyComponentDecrypt, tallyComponentEch0110, tallyComponentEch0222,
					tallyComponentVotesPayloads);
		}
	}

}
