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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import java.nio.file.Path;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the election event IDs in the audit archive (data set) are consistent to the election event ID present in the file electionEventContextPayload.json.
 */
@Component
public class VerifyElectionEventIdConsistency extends AbstractVerification {

    private final ElectionDataExtractionService electionDataExtractionService;

    protected VerifyElectionEventIdConsistency(
            final ApplicationEventPublisher applicationEventPublisher,
            final ElectionDataExtractionService electionDataExtractionService) {
        super(applicationEventPublisher);
        this.electionDataExtractionService = electionDataExtractionService;
    }

    @Override
    public VerificationDefinition getVerificationDefinition() {
        final VerificationDefinition definition = new VerificationDefinition();
        definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
        definition.setCategory(Category.CONSISTENCY);
        definition.setDescription(
                TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification306.description"));
        definition.setId(306);
        definition.setName("VerifyElectionEventIdConsistency");
        definition.addVerifierEvent(SetupEvent.TYPE);
        return definition;
    }

    @Override
    public VerificationResult verify(final Path inputDirectoryPath) {
        final String electionEventId = electionDataExtractionService.getElectionEventContextPayload(inputDirectoryPath)
                .getElectionEventContext().electionEventId();

        final boolean sameElectionEventId = validateControlComponentCodeSharesPayload(inputDirectoryPath, electionEventId) &&
                validateSetupComponentVerificationDataPayload(inputDirectoryPath, electionEventId) &&
                validateSetupComponentTallyDataPayload(inputDirectoryPath, electionEventId) &&
                validateControlComponentPublicKeysPayload(inputDirectoryPath, electionEventId);

        if (sameElectionEventId) {
            return VerificationResult.success(getVerificationDefinition());
        } else {
            return VerificationResult.failure(getVerificationDefinition(),
                    TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification306.nok.message"));
        }
    }

    private boolean validateControlComponentCodeSharesPayload(Path inputDirectoryPath, String electionEventId) {
        return electionDataExtractionService.getControlComponentCodeSharesPayloadsOrderedByNodeId(inputDirectoryPath).stream()
                .allMatch(controlComponentCodeSharesPayload -> electionEventId.equals(controlComponentCodeSharesPayload.getElectionEventId()));
    }

    private boolean validateSetupComponentVerificationDataPayload(Path inputDirectoryPath, String electionEventId) {
        return electionDataExtractionService.getSetupComponentVerificationDataPayloads(inputDirectoryPath).stream()
                .allMatch(setupComponentVerificationDataPayload -> electionEventId.equals(setupComponentVerificationDataPayload.getElectionEventId()));
    }

    private boolean validateSetupComponentTallyDataPayload(Path inputDirectoryPath, String electionEventId) {
        return electionDataExtractionService.getSetupComponentTallyDataPayloads(inputDirectoryPath).stream()
                .allMatch(setupComponentTallyDataPayload -> electionEventId.equals(setupComponentTallyDataPayload.getElectionEventId()));
    }


    private boolean validateControlComponentPublicKeysPayload(Path inputDirectoryPath, String electionEventId) {
        return electionDataExtractionService.getControlComponentPublicKeysPayloads(inputDirectoryPath).stream()
                .allMatch(controlComponentPublicKeysPayload -> electionEventId.equals(controlComponentPublicKeysPayload.getElectionEventId()));
    }
}

