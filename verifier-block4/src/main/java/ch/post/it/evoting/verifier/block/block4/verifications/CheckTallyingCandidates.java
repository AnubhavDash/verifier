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
package ch.post.it.evoting.verifier.block.block4.verifications;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import ch.evoting.xmlns.decrypt._1.Results;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.evoting.xmlns.config._4.CandidatePositionType;
import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.CountMap;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckTallyingCandidates extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition definition = new VerificationDefinition();
		definition.setBlockId(4);
		definition.setId(3);
		definition.setCategory(Category.COMPLETENESS);
		definition
				.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.description"));
		definition.setName("checkTallyingCandidates");
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		//1, config file => map<candidateListId, candidateId> => map1
		PathNode configurationAnonymizedPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		Configuration configuration = Deserializer.fromXml(configurationAnonymizedPathNode.getPath(), Configuration.class);
		Map<String, String> mapConfig = configuration.getContest().getElectionInformation().stream()
				.flatMap(ei -> ei.getList().stream())
				.flatMap(l -> l.getCandidatePosition().stream())
				.filter(cp -> StringUtils.isNotEmpty(cp.getCandidateIdentification()))
				.collect(Collectors.toMap(CandidatePositionType::getCandidateListIdentification,
						CandidatePositionType::getCandidateIdentification, (id1, id2) -> id1));

		// 2, decrypt file => map<countingCircle, map<ListCandidateId||CandidateId, count>> => map2
		PathNode eVotingDecryptResultPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
		Results results = Deserializer.fromXml(eVotingDecryptResultPathNode.getPath(), Results.class);
		Map<String, Map<String, Long>> mapDecrypt = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					String ccId = cc.getCountingCircleIdentification();

					Map<String, Long> answerCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.flatMap(e -> e.getBallot().stream())
									.flatMap(b -> Stream.of(b.getChosenCandidateIdentification().stream(),
											b.getChosenCandidateListIdentification().stream(),
											b.getChosenWriteInsCandidateValue().stream()).flatMap(Function.identity()))
									.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

					return new AbstractMap.SimpleEntry<>(ccId, answerCount);
				})
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(ccId1, ccId2) -> {
							Map<String, Long> concat = new HashMap<>(ccId1);
							ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
							return concat;
						}
				));

		PathNode eCH0110PathNode = pathService.buildFromRootPath(StructureKey.ECH0110, inputDirectoryPath);
		Delivery ech110 = Deserializer.fromXml(eCH0110PathNode.getPath(), Delivery.class);
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.forEach(c -> {
						if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
							String cId = c.getCandidateInformation().getCandidateIdentification();
							BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
							if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
								throw buildVerificationFailureException(
										"The count of votes total for the candidate does not match in majoral election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
										cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.forEach(c -> {
						if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
							String cId = c.getCandidateInformation().getCandidateIdentification();
							BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
							if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
								throw buildVerificationFailureException(
										"The count of votes total for the candidate does not match in proportional election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
										cId);
							}
						}
					});
		});

		//Write Ins
		// writeInsDecryptMap
		Map<String, Map<String, Long>> writeInsDecryptMap = results.getBallotsBox().stream()
				.flatMap(bb -> bb.getCountingCircle().stream())
				.map(cc -> {
					String ccId = cc.getCountingCircleIdentification();

					Map<String, Long> answerCount =
							cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
									.flatMap(e -> e.getBallot().stream())
									.flatMap(b -> b.getChosenWriteInsCandidateValue().stream())
									.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

					return new AbstractMap.SimpleEntry<>(ccId, answerCount);
				})
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(ccId1, ccId2) -> {
							Map<String, Long> concat = new HashMap<>(ccId1);
							ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
							return concat;
						}
				));

		// writeInsEch110Map
		CountMap<String> writeInsEch110Map = new CountMap<>();
		//check writeIns content, fill writeInsEch110Map
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						writeInsEch110Map.increment(cId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.containsKey(cId)) {
								throw buildVerificationFailureException(
										"The count for the candidate does not match in writeIns-containsKey majoral election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
										cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						writeInsEch110Map.increment(cId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.containsKey(cId)) {
								throw buildVerificationFailureException(
										"The count for the candidate does not match in writeIns-containsKey proportional election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
							}
						}
					});
		});

		//check writeIns count
		ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
			String ccId = cc.getCountingCircle().getCountingCircleId();
			cc.getElectionResults().stream()
					.filter(er -> er.getMajoralElection() != null)
					.flatMap(er -> er.getMajoralElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
								throw buildVerificationFailureException(
										"The count for the candidate does not match in writeIns-equals majoral election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
							}
						}
					});
			cc.getElectionResults().stream()
					.filter(er -> er.getProportionalElection() != null)
					.flatMap(er -> er.getProportionalElection().getCandidate().stream())
					.filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
					.forEach(c -> {
						String cId =
								c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
						Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
						if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
							if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
								throw buildVerificationFailureException(
										"The count for the candidate does not match in writeIns-equals proportional election",
										Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
							}
						}
					});
		});

		result.setStatus(Status.OK);
		return result;
	}

	private BigInteger getDecryptCount(Map<String, String> mapConfig, Map<String, Map<String, Long>> mapDecrypt,
			String ccId, String cId) {
		Map<String, Long> countByCC = mapDecrypt.get(ccId);
		if (countByCC == null) {
			throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
		}

		long count = 0;

		//candidateListId
		if (mapConfig.containsValue(cId)) {
			count += mapConfig.entrySet().stream()
					.filter(e -> e.getValue().equals(cId))
					.mapToLong(e -> Optional.ofNullable(countByCC.get(e.getKey())).orElse(0L))
					.sum();
		}

		//candidateId
		count += Optional.ofNullable(countByCC.get(cId)).orElse(0L);

		return BigInteger.valueOf(count);
	}

}
