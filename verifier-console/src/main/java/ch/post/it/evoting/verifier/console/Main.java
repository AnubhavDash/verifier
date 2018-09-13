package ch.post.it.evoting.verifier.console;

import ch.post.it.evoting.verifier.console.dto.Status;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private static final VerifierClient verifierClient = new VerifierClient();


    public static void main(String... args) {
        try {
            if (!verifierClient.ping()) {
                log.error("Server not started");
                System.exit(0);
            }

            verifierClient.reset();
            verifierClient.process();

            Status actualStatus = verifierClient.getCurrentStatus();
            while (!actualStatus.getStatus().equalsIgnoreCase("COMPLETED")) {
                log.info(actualStatus.getStatus());
                Thread.sleep(1000);
                actualStatus = verifierClient.getCurrentStatus();
            }
            log.info(actualStatus.getStatus());

            verifierClient.getTests().forEach(t -> {
                Consumer<Object> logger = t.getStatus().equalsIgnoreCase("KO") ? log::error : log::info;
                logger.accept(String.format("Test %s : %s %s", t.getId(), t.getStatus(), Optional.ofNullable(t.getMessage()).orElse("")));
            });

            //verifierClient.shutdown();
        } catch (InterruptedException e) {
            log.fatal("fatal error", e);
        }
    }


}
