/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.verifications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckConfirmationsMappingTable extends AbstractVerification {

	private static final Pattern PCCOMP_PARTIAL_CODE_SUCCESSFULLY_COMPUTED_PATTERN =
			Pattern.compile(".*\\|PCCOMP|.*\\|Partial code successfully computed\\|.*");

	private static final Pattern PCCOMP_ELEMENT_PATTERN = Pattern.compile("#pc_comp=\"\\[(.+?)]\"");

	private static final Pattern REQUEST_ID_ELEMENT_PATTERN = Pattern.compile("#request_id=\"(.+?)\"");

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(2);
		def.setCategory(Category.INTEGRITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification09.description"));
		def.setId(9);
		def.setName("checkConfirmationsMappingTable");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Build host to control component map.
		Map<String, String> hostToControlComponentMap = buildHostToControlComponentMap(inputDirectoryPath);

		// Process every single secure (host) log file and build a map of request id list for each control component.
		// While processing the files, two duplicates checks are done :
		//  1. no duplicates exist in a single host data.
		//  2. no duplicates exist in the overall of all hosts of a single control component.
		// In case of a duplicate, throw an error.
		Map<String, List<String>> controlComponentToRequestIdsListMap =
				processSecureLogFiles(inputDirectoryPath, hostToControlComponentMap);

		List<String> controlComponents = new ArrayList<>(controlComponentToRequestIdsListMap.keySet());
		String firstControlComponent = controlComponents.remove(0);

		// For each request id of the first control component, remove this request id from the other control
		// components. If such request id could not be found in another control component, throw an error.
		// This case would mean that the first control component contains a specific request id which doesn't exist
		// for an other component.
		matchingRequestIdForEachControlComponentCheck(controlComponentToRequestIdsListMap, controlComponents,
				firstControlComponent);

		// For each of the other control components (not the first), if any request id remains, throw an error.
		// This case would mean that the first control component didn't contain that specific request id.
		emptyRequestIdListCheck(controlComponentToRequestIdsListMap, controlComponents);

		result.setStatus(Status.OK);
		return result;
	}

	boolean logEntryMatchCriteria(String line) {
		return PCCOMP_PARTIAL_CODE_SUCCESSFULLY_COMPUTED_PATTERN.matcher(line).matches();
	}

	boolean hasExactlyOnePCCompElement(String line) {
		Matcher matcher = PCCOMP_ELEMENT_PATTERN.matcher(line);

		return matcher.find() && matcher.group(1).split(",").length == 1;
	}

	String extractRequestId(String line) {
		Matcher matcher = REQUEST_ID_ELEMENT_PATTERN.matcher(line);

		if (matcher.find()) {
			return matcher.group(1);
		}

		throw new RuntimeException("No #request_id found for line " + line);
	}

	private void controlComponentDuplicatesCheck(Map<String, List<String>> controlComponentToRequestIdsListMap) {
		controlComponentToRequestIdsListMap.forEach((controlComponent, requestIdList) -> {
			Set<String> requestIdSet = new HashSet<>(requestIdList);

			if (requestIdList.size() != requestIdSet.size()) {
				throw new RuntimeException("Duplicate request_id found for control component " + controlComponent);
			}
		});
	}

	private void matchingRequestIdForEachControlComponentCheck(
			Map<String, List<String>> controlComponentToRequestIdsListMap,
			List<String> controlComponents, String firstControlComponent) {
		for (String requestId : controlComponentToRequestIdsListMap.get(firstControlComponent)) {
			for (String controlComponent : controlComponents) {
				boolean hasBeenRemoved = controlComponentToRequestIdsListMap.get(controlComponent).remove(requestId);
				if (!hasBeenRemoved) {
					throw buildVerificationFailureException(
							"The request [" + requestId + "] cannot be found for all control components",
							Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification09.nok.message");
				}
			}
		}
	}

	private Map<String, List<String>> processSecureLogFiles(Path inputDirectoryPath,
			Map<String, String> hostToControlComponentMap) throws IOException {

		// Build control component to empty request id list map.
		Map<String, List<String>> controlComponentToRequestIdsListMap = hostToControlComponentMap
				.values()
				.stream()
				.distinct()
				.collect(Collectors.toMap(Function.identity(), controlComponent -> new ArrayList<>()));

		PathNode secureLogsDirectories = pathService.buildFromRootPath(StructureKey.SECURE_LOG_DIR, inputDirectoryPath);
		for (Path secureLogDirectory : secureLogsDirectories.getRegexPaths()) {
			PathNode secureLogs = pathService.buildFromDynamicAncestorPath(StructureKey.SECURE_LOG, secureLogDirectory);
			for (Path secureLog : secureLogs.getRegexPaths()) {
				List<String> requestIdList =
						Files.lines(secureLog)
								.parallel()
								.filter(this::logEntryMatchCriteria)
								.filter(this::hasExactlyOnePCCompElement)
								.map(this::extractRequestId)
								.collect(Collectors.toList());

				Set<String> requestIdSet = new HashSet<>(requestIdList);

				if (requestIdList.size() != requestIdSet.size()) {
					throw new RuntimeException("Duplicate request_id found in file " + secureLog.getFileName());
				}

				// Add computed request_id list to corresponding control component.
				String hostName = secureLogDirectory.getFileName().toString();
				String controlComponent = hostToControlComponentMap.get(hostName);

				controlComponentToRequestIdsListMap.get(controlComponent).addAll(requestIdList);
			}
		}

		// For each control component, check if any duplicates exist after merging all its children's hosts data.
		controlComponentDuplicatesCheck(controlComponentToRequestIdsListMap);

		return controlComponentToRequestIdsListMap;
	}

	private void emptyRequestIdListCheck(Map<String, List<String>> controlComponentToRequestIdsListMap,
			List<String> controlComponents) {
		for (String controlComponent : controlComponents) {
			for (String requestId : controlComponentToRequestIdsListMap.get(controlComponent)) {
				throw buildVerificationFailureException(
						"The request [" + requestId + "] cannot be found for all control components",
						Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification09.nok.message");
			}
		}
	}

	private Map<String, String> buildHostToControlComponentMap(Path inputDirectoryPath) throws IOException {
		PathNode mappingCcHostsPathNode = pathService.buildFromRootPath(StructureKey.MAPPING_CC_HOSTS, inputDirectoryPath);
		return Files.lines(mappingCcHostsPathNode.getPath())
				.skip(1) // Skip header line
				.parallel()
				.filter(StringUtils::isNotBlank)
				.map(line -> line.split(";"))
				.filter(lineArray -> lineArray.length > 1)
				.collect(Collectors.toMap(lineArray -> lineArray[0], lineArray -> lineArray[1]));
	}

}
