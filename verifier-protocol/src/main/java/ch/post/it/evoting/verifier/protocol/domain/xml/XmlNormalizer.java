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
package ch.post.it.evoting.verifier.protocol.domain.xml;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ech.xmlns.ech_0110._4.CandidateResultType;
import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.ech.xmlns.ech_0110._4.ElectionResultType;
import ch.ech.xmlns.ech_0110._4.EventResultDelivery;
import ch.ech.xmlns.ech_0222._1.ElectionRawDataType;
import ch.ech.xmlns.ech_0222._1.EventRawDataDelivery;
import ch.ech.xmlns.ech_0222._1.RawDataType;

public class XmlNormalizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlNormalizer.class);

	/**
	 * Normalize recursively write-ins in the Delivery object for ech-0110
	 *
	 * @param toNormalize the object to normalize
	 * @return the object normalized in place
	 */
	public Delivery normalizeWriteInsEch0110(final Delivery toNormalize) {
		Optional.ofNullable(toNormalize)
				.map(Delivery::getResultDelivery)
				.map(EventResultDelivery::getCountingCircleResults)
				.orElse(Collections.emptyList())
				.forEach(countingCircleResultsType -> Optional.ofNullable(countingCircleResultsType.getElectionGroupResults())
						.orElse(Collections.emptyList())
						.forEach(electionGroupResultType -> Optional.ofNullable(electionGroupResultType.getElectionResults())
								.orElse(Collections.emptyList())
								.forEach(electionResult -> {
											Optional.ofNullable(electionResult.getMajoralElection())
													.map(ElectionResultType.MajoralElection::getCandidate)
													.orElse(Collections.emptyList())
													.forEach(candidateResultType ->
															normalizeWriteIn(candidateResultType,
																	CandidateResultType::getWriteIn,
																	CandidateResultType::setWriteIn));

											Optional.ofNullable(electionResult.getProportionalElection()).map(
															ElectionResultType.ProportionalElection::getCandidate)
													.orElse(Collections.emptyList())
													.forEach(candidateResultType ->
															normalizeWriteIn(candidateResultType,
																	CandidateResultType::getWriteIn,
																	CandidateResultType::setWriteIn));

											Optional.ofNullable(electionResult.getElectedCandidate())
													.orElse(Collections.emptyList())
													.forEach(electedCandidate ->
															normalizeWriteIn(electedCandidate,
																	ElectionResultType.ElectedCandidate::getWriteIn,
																	ElectionResultType.ElectedCandidate::setWriteIn));
										}
								)
						)
				);

		//This should not be necessary since the DeliveryMapper does not create RawData, but we provide it for completeness.
		Optional.ofNullable(toNormalize)
				.map(Delivery::getResultDelivery)
				.map(EventResultDelivery::getRawData)
				.ifPresent(this::normalizeWriteInsEch0220RawData);

		return toNormalize;
	}

	/**
	 * Normalize recursively write-ins in the Delivery object for ech-0220
	 *
	 * @param toNormalize the object to normalize
	 * @return the object normalized in place
	 */
	public ch.ech.xmlns.ech_0222._1.Delivery normalizeWriteInsEch0220(final ch.ech.xmlns.ech_0222._1.Delivery toNormalize) {
		Optional.ofNullable(toNormalize)
				.map(ch.ech.xmlns.ech_0222._1.Delivery::getRawDataDelivery)
				.map(EventRawDataDelivery::getRawData)
				.ifPresent(this::normalizeWriteInsEch0220RawData);

		return toNormalize;
	}

	private void normalizeWriteInsEch0220RawData(final RawDataType toNormalize) {
		Optional.ofNullable(toNormalize)
				.map(RawDataType::getCountingCircleRawData)
				.orElse(Collections.emptyList())
				.forEach(countingCircleRawData -> Optional.ofNullable(countingCircleRawData.getElectionGroupBallotRawData())
						.orElse(Collections.emptyList())
						.forEach(electionGroupBallotRawData -> electionGroupBallotRawData.getElectionRawData()
								.forEach(electionRawDataType -> electionRawDataType.getBallotRawData()
										.forEach(ballotRawData -> ballotRawData.getBallotPosition()
												.forEach(ballotPosition ->
														Optional.ofNullable(ballotPosition.getCandidate())
																.ifPresent(candidate -> normalizeWriteIn(candidate,
																		ElectionRawDataType.BallotRawData.BallotPosition.Candidate::getWriteIn,
																		ElectionRawDataType.BallotRawData.BallotPosition.Candidate::setWriteIn)
																)
												)
										)
								)
						)
				);
	}

	private <T> void normalizeWriteIn(T object, Function<T, String> getWriteIn, BiConsumer<T, String> setWriteIn) {
		Optional.ofNullable(getWriteIn.apply(object))
				.ifPresent(writeIn -> {
					final String xsTokenNormalizedWriteIn = XmlUtils.xsTokenNormalize(writeIn);
					String normalizedWriteIn = xsTokenNormalizedWriteIn.equals("") ? "-" : xsTokenNormalizedWriteIn;
					if (!normalizedWriteIn.equals(writeIn)) {
						LOGGER.info("Write-in was normalized. [original: {}, normalized: {}]", writeIn, normalizedWriteIn);
					}
					setWriteIn.accept(object, normalizedWriteIn);
				});
	}

}
