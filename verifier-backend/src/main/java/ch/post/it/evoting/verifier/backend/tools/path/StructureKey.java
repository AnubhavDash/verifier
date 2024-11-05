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
package ch.post.it.evoting.verifier.backend.tools.path;

public enum StructureKey {

	CONTEXT_DIR,
	CONTEXT_MANIFEST,
	CONFIGURATION_ANONYMIZED,
	ELECTION_EVENT_CONTEXT,
	SETUP_COMPONENT_PUBLIC_KEYS,
	CONTROL_COMPONENT_PUBLIC_KEYS,
	CONTEXT_VERIFICATION_CARD_SETS_DIR,
	CONTEXT_VERIFICATION_CARD_SET_ID_DIR,
	SETUP_COMPONENT_TALLY_DATA,

	TALLY_DIR,
	TALLY_MANIFEST,
	TALLY_COMPONENT_ECH0222,
	BALLOT_BOXES_DIR,
	BALLOT_BOX_ID_DIR,
	CONTROL_COMPONENT_BALLOT_BOX,
	CONTROL_COMPONENT_SHUFFLE,
	TALLY_COMPONENT_SHUFFLE,
	TALLY_COMPONENT_VOTES
}
