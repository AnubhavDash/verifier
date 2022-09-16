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
package ch.post.it.evoting.verifier.backend.tools.path;

public enum StructureKey {

	SETUP_DIR,
	CONFIGURATION_ANONYMIZED,
	ELECTION_EVENT_CONTEXT,
	CONTROL_COMPONENT_PUBLIC_KEYS,
	ENCRYPTION_PARAMETERS,
	VERIFICATION_CARD_SETS_DIR,
	VERIFICATION_CARD_SET_ID_DIR,
	CONTROL_COMPONENT_CODE_SHARES,
	SETUP_COMPONENT_VERIFICATION_DATA,
	SETUP_COMPONENT_TALLY_DATA,

	TALLY_DIR,
	BALLOT_BOXES_DIR,
	BALLOT_BOX_ID_DIR,
	CONTROL_COMPONENT_BALLOT_BOX,
	CONTROL_COMPONENT_SHUFFLE,
	TALLY_COMPONENT_SHUFFLE,
	TALLY_COMPONENT_VOTES
}
