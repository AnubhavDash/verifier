/**
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
package ch.post.it.evoting.verifier.controller;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.dto.ExecutionStatus;
import ch.post.it.evoting.verifier.dto.LifecycleStatus;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class VerifierController {

    private VerifierProcessor processor;

    private ExecutionStatus executionStatus;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    public VerifierController(VerifierProcessor processor) {
        this.processor = processor;

        initializeExecutionStatus();

        processor.registerProcessListener(t -> {
            notifyUpdate(t);
            incrementStatus();
            updateIfCompleted();
        });
    }

    private void initializeExecutionStatus() {
        this.executionStatus = ExecutionStatus.builder()
                .testActual(0)
                .testCount(this.processor.getVerificationStatus().size())
                .status(LifecycleStatus.NOT_STARTED).build();
    }

    @GetMapping("/ping")
    public boolean ping() {
        return true;
    }

    @GetMapping("/shutdown")
    public void shutdown() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        final long DELAY_BEFORE_EXECUTION = 10;
        executor.schedule(() -> System.exit(0), DELAY_BEFORE_EXECUTION, TimeUnit.MILLISECONDS);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public void reset() {
        this.processor.resetExecution();
        this.initializeExecutionStatus();
    }

    @RequestMapping(value = "/verifications/*.pdf", method = RequestMethod.GET, produces = "application/pdf")
    public byte[] generatePdf(Locale locale) {
        return this.processor.generatePdf(getLanguage(locale));
    }

    @GetMapping("/status")
    public ExecutionStatus getStatus() {
        return this.executionStatus;
    }

    @GetMapping(value = "/configurationInputDirectory")
    public Configuration getConfiguration() {
        return this.processor.getConfiguration();
    }

    @RequestMapping(value = "/configurationInputDirectory", method = RequestMethod.POST)
    public void setConfigurationInputDirectory(@RequestBody Configuration value) {
        this.processor.setConfiguration(value);
    }

    @GetMapping("/verifications")
    public List<Verification> getTestStatus() {
        return this.processor.getVerificationStatus();
    }

    @RequestMapping(value = "/verifications", method = RequestMethod.POST)
    public ResponseEntity process( @RequestParam(required = false) String runOptions ) {
        this.executionStatus.setStatus(LifecycleStatus.RUNNING);
        try {
            Set<VerificationTrait> traits = getTraits(runOptions);
            this.processor.processVerifications(traits);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AlreadyStartedException e) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Process already started");
        }
    }

    /*
        Converts a comma separated list to a list of test traits
     */
    protected Set<VerificationTrait> getTraits(String runOptions) {
        Set<VerificationTrait> traits = null;
        if ( runOptions != null ) {
            traits = Arrays.asList(runOptions.split(",")).stream()
                    .map(t -> VerificationTrait.fromValue(t))
                    .collect(Collectors.toSet());
        }
        return traits;
    }

    protected void notifyUpdate(Verification executionStatus) {
        this.template.convertAndSend("/pushUpdate", executionStatus);
    }

    protected void incrementStatus() {
        this.executionStatus.setTestActual(this.getStatus().getTestActual() + 1);
    }

    protected void updateIfCompleted() {
        if (this.getStatus().getTestActual() == this.getStatus().getTestCount()) {
            this.executionStatus.setStatus(LifecycleStatus.COMPLETED);
        }
    }

    private Language getLanguage(Locale locale) {
        Language language = Language.DE;
        Optional<Language> optLanguage = Arrays.stream(Language.values())
                .filter(l -> l.getLocale().getLanguage().equalsIgnoreCase(locale.getLanguage()))
                .findFirst();
        if (optLanguage.isPresent()) {
            language = optLanguage.get();
        }
        return language;
    }

}
