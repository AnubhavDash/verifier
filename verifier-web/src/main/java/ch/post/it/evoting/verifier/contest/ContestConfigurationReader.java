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
package ch.post.it.evoting.verifier.contest;

import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.config._4.ContestDescriptionInformationType;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@Component
public class ContestConfigurationReader {

    private static final Logger LOGGER = Logger.getLogger(ContestConfigurationReader.class);

    private static final String ELECTION_SETUP_PATH = "election_setup";


    private Configuration loadConfiguration(String inputDirectory) {
        try {
            return Deserializer.fromXml(Paths.get(inputDirectory).resolve(ELECTION_SETUP_PATH).toFile(), "configuration-anonymized.xml", Configuration.class);
        } catch (FileNotFoundException e) {
            LOGGER.error("Unable to locate the configuration-anonymized file based on given configuration directory");
        } catch (IOException | JAXBException | XMLStreamException e) {
            LOGGER.error(String.format("unable to get the contest name in file %s/configuration-anonymized.xml", inputDirectory), e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getContestName(String inputDirectory, Language language) {
        Configuration configuration = loadConfiguration(inputDirectory);
        if (configuration != null) {
            Optional<ContestDescriptionInformationType.ContestDescriptionInfo> optDesc = configuration.getContest().getContestDescription().getContestDescriptionInfo().stream().filter(o -> o.getLanguage().value().equalsIgnoreCase(language.toString())).findFirst();
            if (optDesc.isPresent()) {
                return optDesc.get().getContestDescription();
            } else {
                LOGGER.warn(String.format("language %s not found for contestDescription", language));
            }
        }
        return "";
    }

    public Date getContestDate(String inputDirectory) {
        Configuration configuration = loadConfiguration(inputDirectory);
        if (configuration != null) {
            return configuration.getContest().getContestDate().toGregorianCalendar().getTime();
        }
        return null;
    }

}

