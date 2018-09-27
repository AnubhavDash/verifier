/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.util;

import ch.evoting.xmlns.config._3.Configuration;
import ch.evoting.xmlns.config._3.ContestDescriptionInformationType;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

@Component
public class ContestConfigurationReader {

    private static final String ELECTION_SETUP_PATH = "election_setup";
    @Value("${inputDirectory}")
    private String configurationInputDirectory;

    public String getContestName(Language language) {
        try {
            Configuration configuration = Deserializer.fromXml(Paths.get(configurationInputDirectory).resolve(ELECTION_SETUP_PATH).toFile(), "configuration-anonymized.xml", Configuration.class);
            Optional<ContestDescriptionInformationType.ContestDescriptionInfo> optDesc = configuration.getContest().getContestDescription().getContestDescriptionInfo().stream().filter(o -> o.getLanguage().value().equalsIgnoreCase(language.toString())).findFirst();
            if (optDesc.isPresent()) {
                return optDesc.get().getContestDescription();
            } else {
                return "";
            }
        } catch (IOException | JAXBException e) {
            //TODO logger
            throw new RuntimeException(e);
        }
    }

}

