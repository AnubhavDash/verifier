package ch.post.it.evoting.verifier.controller;

import ch.post.it.evoting.verifier.dto.ExecutionStatus;
import ch.post.it.evoting.verifier.dto.Status;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        processor.registerProcessListener(executionStatus -> {
            notifyUpdate(executionStatus);
            this.executionStatus.setTestActual(this.getStatus().getTestActual() + 1);

            if (this.getStatus().getTestActual() == this.getStatus().getTestCount()) {
                this.executionStatus.setStatus(Status.COMPLETED);
            }
        });
    }

    protected void initializeExecutionStatus() {
        this.executionStatus = ExecutionStatus.builder()
                .testActual(0)
                .testCount(this.processor.getTestStatus().size())
                .status(Status.NOT_STARTED).build();
    }

    @GetMapping("/ping")
    public boolean ping() {
        return true;
    }

    @GetMapping("/shutdown")
    public void shutdown() {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> System.exit(0), 10, TimeUnit.MILLISECONDS);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public void reset() {
        this.processor.resetExecution();
        this.initializeExecutionStatus();
    }

    @GetMapping("/status")
    public ExecutionStatus getStatus() {
        return this.executionStatus;
    }

    @GetMapping("/tests")
    public List<Test> getTestStatus() {
        return this.processor.getTestStatus();
    }

    @RequestMapping(value = "/tests", method = RequestMethod.POST)
    public ResponseEntity putMessage() {
        this.executionStatus.setStatus(Status.RUNNING);
        try {
            this.processor.processTests();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AlreadyStartedException e) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Process already started");
        }
    }

    protected void notifyUpdate(Test executionStatus) {
        this.template.convertAndSend("/pushUpdate", executionStatus);
    }
}
