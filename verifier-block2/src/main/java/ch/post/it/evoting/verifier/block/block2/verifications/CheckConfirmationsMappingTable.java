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

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CheckConfirmationsMappingTable extends AbstractVerification {

    public static final Pattern PCCOMP_PARTIAL_CODE_SUCCESSFULLY_COMPUTED_PATTERN =
            Pattern.compile(".*\\|PCCOMP|.*\\|Partial code successfully computed\\|.*");

    private static final Pattern PCCOMP_ELEMENT_PATTERN = Pattern.compile("#pc_comp=\"\\[(.+?)]\"");

    private static final Pattern REQUEST_ID_ELEMENT_PATTERN = Pattern.compile("#request_id=\"(.+?)\"");

    private static final String MAPPINGS_CC_HOSTS_FILENAME_PATTERN = "mapping_cc_hosts.csv";
    private static final String SECURELOG_FILENAME_PATTERN = "(cv|cg)_secure-.*\\.log";

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

        Path pathSecureLogs = inputDirectoryPath.resolve(Block2VerificationSuite.PATH_SECURE_LOGS);

        // Build host to control component map.
        Map<String, String> hostToControlComponentMap = buildHostToControlComponentMap(pathSecureLogs);

        // Process every single secure (host) log file and build a map of request id list for each control component.
        // While processing the files, two duplicates checks are done :
        //  1. no duplicates exist in a single host data.
        //  2. no duplicates exist in the overall of all hosts of a single control component.
        // In case of a duplicate, throw an error.
        Map<String, List<String>> controlComponentToRequestIdsListMap =
                processSecureLogFiles(pathSecureLogs, hostToControlComponentMap);

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

    void controlComponentDuplicatesCheck(Map<String, List<String>> controlComponentToRequestIdsListMap) {
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

    private Map<String, List<String>> processSecureLogFiles(Path pathSecureLogs,
                                                            Map<String, String> hostToControlComponentMap) throws IOException {

        // Build control component to empty request id list map.
        Map<String, List<String>> controlComponentToRequestIdsListMap = hostToControlComponentMap
                .values()
                .stream()
                .distinct()
                .collect(Collectors.toMap(Function.identity(), controlComponent -> new ArrayList<>()));

        for (Path path : PathHelper.getPaths(pathSecureLogs, 4, SECURELOG_FILENAME_PATTERN)) {
            List<String> requestIdList =
                    Files.lines(path)
                            .parallel()
                            .filter(this::logEntryMatchCriteria)
                            .filter(this::hasExactlyOnePCCompElement)
                            .map(this::extractRequestId)
                            .collect(Collectors.toList());

            Set<String> requestIdSet = new HashSet<>(requestIdList);

            if (requestIdList.size() != requestIdSet.size()) {
                throw new RuntimeException("Duplicate request_id found in file " + path.getFileName());
            }

            // Add computed request_id list to corresponding control component.
            String hostName = path.getParent().getParent().getParent().getFileName().toString();
            String controlComponent = hostToControlComponentMap.get(hostName);

            controlComponentToRequestIdsListMap.get(controlComponent).addAll(requestIdList);
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

    private Map<String, String> buildHostToControlComponentMap(Path pathSecureLogs) throws IOException {
        return Files.lines(PathHelper.getPath(pathSecureLogs, 1, MAPPINGS_CC_HOSTS_FILENAME_PATTERN))
                .skip(1) // Skip header line
                .parallel()
                .filter(StringUtils::isNotBlank)
                .map(line -> line.split(";"))
                .filter(lineArray -> lineArray.length > 1)
                .collect(Collectors.toMap(lineArray -> lineArray[0], lineArray -> lineArray[1]));
    }

}
