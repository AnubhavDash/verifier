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
package ch.post.it.evoting.verifier.contest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.config._4.ContestDescriptionInformationType;
import ch.post.it.evoting.verifier.plugin.contract.Language;
import ch.post.it.evoting.verifier.core.internal.tools.Deserializer;

@Component
public class ContestConfigurationReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContestConfigurationReader.class);

	private static final String ELECTION_SETUP_PATH = "election_setup";

	private Configuration loadConfiguration(String inputDirectory) {
		try {
			return Deserializer
					.fromXml(Paths.get(inputDirectory).resolve(ELECTION_SETUP_PATH).toFile(), "configuration-anonymized.xml", Configuration.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to locate the configuration-anonymized file based on given configuration directory");
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new IllegalArgumentException(
					String.format("unable to get the contest name in file %s/configuration-anonymized.xml", inputDirectory), e);
		}
		return null;
	}

	public String getContestName(String inputDirectory, Language language) {
		var configuration = loadConfiguration(inputDirectory);
		if (configuration != null) {
			Optional<ContestDescriptionInformationType.ContestDescriptionInfo> optDesc = configuration.getContest().getContestDescription()
					.getContestDescriptionInfo().stream().filter(o -> o.getLanguage().value().equalsIgnoreCase(language.toString())).findFirst();
			if (optDesc.isPresent()) {
				return optDesc.get().getContestDescription();
			} else {
				LOGGER.warn("language {} not found for contestDescription", language);
			}
		}
		return "";
	}

	public Date getContestDate(String inputDirectory) {
		var configuration = loadConfiguration(inputDirectory);
		if (configuration != null) {
			return configuration.getContest().getContestDate().toGregorianCalendar().getTime();
		}
		return null;
	}

}

