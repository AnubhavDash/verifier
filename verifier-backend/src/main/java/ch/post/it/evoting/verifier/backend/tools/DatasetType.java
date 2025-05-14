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
package ch.post.it.evoting.verifier.backend.tools;

import java.util.List;

import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;

public enum DatasetType {
	CONTEXT(List.of(StructureKey.CONFIGURATION_ANONYMIZED, StructureKey.ELECTION_EVENT_CONTEXT, StructureKey.SETUP_COMPONENT_PUBLIC_KEYS,
			StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, StructureKey.SETUP_COMPONENT_TALLY_DATA)),
	SETUP(List.of(StructureKey.CONTROL_COMPONENT_CODE_SHARES, StructureKey.SETUP_COMPONENT_VERIFICATION_DATA)),
	TALLY(List.of(StructureKey.TALLY_COMPONENT_DECRYPT, StructureKey.TALLY_COMPONENT_ECH0110, StructureKey.TALLY_COMPONENT_ECH0222,
			StructureKey.CONTROL_COMPONENT_BALLOT_BOX, StructureKey.CONTROL_COMPONENT_SHUFFLE, StructureKey.TALLY_COMPONENT_SHUFFLE,
			StructureKey.TALLY_COMPONENT_VOTES));

	private final List<StructureKey> structureKeys;

	DatasetType(final List<StructureKey> structureKeys) {
		this.structureKeys = structureKeys;
	}

	public List<StructureKey> getStructureKeys() {
		return List.copyOf(structureKeys);
	}
}
