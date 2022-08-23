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
package ch.post.it.evoting.verifier.protocol.domain;

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;

import java.math.BigInteger;

import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

/**
 * Centralizes the addition of the context data to each message - according to the section "Channel Security" in the system specification.
 */
public final class ChannelSecurityContextData {

	public static final String NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE = "The node id must be part of the known node ids. [nodeId: %s]";

	private ChannelSecurityContextData() {
		// utility class
	}

	/**
	 * @return The additional context data for the message ( p, q, g, seed, <b>p</b> ).
	 */
	public static Hashable setupComponentEncryptionParameters() {
		return HashableString.from("encryption parameters");
	}

	/**
	 * @param nodeId          j, the node id. Must be part of the known node ids.
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( pk<sub>CCRj</sub> , EL<sub>pk,j</sub> , πEL<sub>pk,j</sub> ).
	 * @throws NullPointerException      if the election event id is null.
	 * @throws FailedValidationException if the election event id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentPublicKeys(final int nodeId, final String electionEventId) {
		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);

		return HashableList.of(
				HashableString.from("OnlineCC keys"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( {vc<sub>id</sub>, K<sub>id</sub>, c<sub>pCC,id</sub>,
	 * c<sub>ck,id</sub>}<sup>N_E-1</sup><sub>id=0</sub>, L<sub>pCC</sub> ).
	 * @throws NullPointerException      if any id is null.
	 * @throws FailedValidationException if any id is not a valid UUID.
	 */
	public static Hashable setupComponentVerificationData(final String electionEventId, final String verificationCardSetId) {
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("verification data"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param nodeId                j, the node id. Must be part of the known node ids.
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message (Kj ,Kcj , cexpPCC,j , cexpCK,j , πexpPCC,j , πexpCK,j ).
	 * @throws NullPointerException      if the election event id or the verification card set id is null.
	 * @throws FailedValidationException if the election event id or the verification card set id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentCodeShares(final int nodeId, final String electionEventId, final String verificationCardSetId) {
		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("encrypted code shares"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message L<sub>lVCC</sub>.
	 * @throws NullPointerException      if the election event id or the verification card set id is null.
	 * @throws FailedValidationException if the election event id or the verification card set id is not a valid UUID.
	 */
	public static Hashable setupComponentLVCCAllowList(final String electionEventId, final String verificationCardSetId) {
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("lvcc allow list"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message CMtable.
	 * @throws NullPointerException      if the election event id or the verification card set id is null.
	 * @throws FailedValidationException if the election event id or the verification card set id is not a valid UUID.
	 */
	public static Hashable setupComponentCMTable(final String electionEventId, final String verificationCardSetId) {
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("cm table"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message VCks.
	 * @throws NullPointerException      if the election event id or the verification card set id is null.
	 * @throws FailedValidationException if the election event id or the verification card set id is not a valid UUID.
	 */
	public static Hashable setupComponentVerificationCardKeystores(final String electionEventId, final String verificationCardSetId) {
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("vc keystore"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( {pk<sub>CCRj</sub>}<sup>4</sup><sub>j=1</sub>, pk<sub>CCR</sub>,
	 * {ELpk,j}<sup>4</sup><sub>j=1</sub>, EB<sub>pk</sub>, EL<sub>pk</sub> ).
	 * @throws NullPointerException      if the election event id is null.
	 * @throws FailedValidationException if the election event id is not a valid UUID.
	 */
	public static Hashable setupComponentPublicKeys(final String electionEventId) {
		validateUUID(electionEventId);

		return HashableList.of(
				HashableString.from("public keys"),
				HashableString.from("setup"),
				HashableString.from(electionEventId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( vc, K, pTable ).
	 * @throws NullPointerException      if the election event id or the verification card set id is null.
	 * @throws FailedValidationException if the election event id or the verification card set id is not a valid UUID.
	 */
	public static Hashable setupComponentTallyData(final String electionEventId, final String verificationCardSetId) {
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);

		return HashableList.of(
				HashableString.from("tally data"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( E1, E2, E&#771;1, π<sub>Exp</sub>, π<sub>EqEnc</sub> ).
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 */
	public static Hashable votingServerEncryptedVote(final String electionEventId, final String verificationCardSetId,
			final String verificationCardId) {

		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(
				HashableString.from("encrypted vote"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param nodeId                j, the node id. Must be part of the known node ids.
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( d<sub>j</sub>, π<sub>decPCC,j</sub> ).
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentPartialDecrypt(final int nodeId, final String electionEventId, final String verificationCardSetId,
			final String verificationCardId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(
				HashableString.from("partial decrypt"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param nodeId                j, the node id. Must be part of the known node ids.
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message lCC<sub>j,id</sub>.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentLCCShare(final int nodeId, final String electionEventId, final String verificationCardSetId,
			final String verificationCardId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(
				HashableString.from("lcc share"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message CK<sub>id</sub>.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 */
	public static Hashable votingServerConfirm(final String electionEventId, final String verificationCardSetId, final String verificationCardId) {

		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(
				HashableString.from("confirmation key"),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param nodeId                j, the node id. Must be part of the known node ids.
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message hlVCC<sub>id,j</sub>.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponenthlVCC(final int nodeId, final String electionEventId, final String verificationCardSetId,
			final String verificationCardId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(
				HashableString.from("hlvcc"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param nodeId                j, the node id. Must be part of the known node ids.
	 * @param electionEventId       ee, the election event id. Must be non-null and a valid UUID.
	 * @param verificationCardSetId vcs, the verification card set id. Must be non-null and a valid UUID.
	 * @param verificationCardId    vc<sub>id</sub>, the verification card id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message lVCC<sub>id,j</sub>.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentlVCCShare(final int nodeId, final String electionEventId, final String verificationCardSetId,
			final String verificationCardId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(verificationCardSetId);
		validateUUID(verificationCardId);

		return HashableList.of(HashableString.from("lvcc share"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(verificationCardSetId),
				HashableString.from(verificationCardId));
	}

	/**
	 * @param nodeId          j, the node id. Must be part of the known node ids.
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @param ballotBoxId     bb, the ballot box id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message c<sub>init,1</sub>.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentBallotBox(final int nodeId, final String electionEventId, final String ballotBoxId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);

		return HashableList.of(
				HashableString.from("ballotbox"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(ballotBoxId));
	}

	/**
	 * @param nodeId          j, the node id. Must be part of the known node ids.
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @param ballotBoxId     bb, the ballot box id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message (c<sub>mix,j</sub>, π<sub>mix,j</sub>, c<sub>dec,j</sub>, π<sub>dec,j</sub>).
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 * @throws IllegalArgumentException  if the node id is not part of the known node ids.
	 */
	public static Hashable controlComponentShuffle(final int nodeId, final String electionEventId, final String ballotBoxId) {

		checkArgument(NODE_IDS.contains(nodeId), NODE_ID_INPUT_VALIDATION_ERROR_MESSAGE, nodeId);
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);

		return HashableList.of(
				HashableString.from("shuffle"),
				HashableBigInteger.from(BigInteger.valueOf(nodeId)),
				HashableString.from(electionEventId),
				HashableString.from(ballotBoxId));
	}

	/**
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @param ballotBoxId     bb, the ballot box id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( c<sub>mix,5</sub>, π<sub>mix,5</sub>, m, π<sub>dec,5</sub>).
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 */
	public static Hashable tallyComponentShuffle(final String electionEventId, final String ballotBoxId) {

		validateUUID(electionEventId);
		validateUUID(ballotBoxId);

		return HashableList.of(
				HashableString.from("shuffle"),
				HashableString.from("offline"),
				HashableString.from(electionEventId),
				HashableString.from(ballotBoxId));
	}

	/**
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @param ballotBoxId     bb, the ballot box id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message Lvotes.
	 * @throws NullPointerException      if any input id is null.
	 * @throws FailedValidationException if any input id is not a valid UUID.
	 */
	public static Hashable tallyComponentVotes(final String electionEventId, final String ballotBoxId) {

		validateUUID(electionEventId);
		validateUUID(ballotBoxId);

		return HashableList.of(
				HashableString.from("decoded votes"),
				HashableString.from(electionEventId),
				HashableString.from(ballotBoxId));
	}

	/**
	 * @param electionEventId ee, the election event id. Must be non-null and a valid UUID.
	 * @return The additional context data for the message ( Hash_1, ..., Hash_k ).
	 * @throws NullPointerException      if the election event id is null.
	 * @throws FailedValidationException if the election event id is not a valid UUID.
	 */
	public static Hashable setupComponentElectoralBoardHashes(final String electionEventId) {
		validateUUID(electionEventId);

		return HashableList.of(
				HashableString.from("electoral board hashes"),
				HashableString.from(electionEventId));
	}

}
