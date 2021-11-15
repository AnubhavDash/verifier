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
package ch.post.it.evoting.verifier.processor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.contest.ContestConfigurationReader;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.mapper.VerificationMapper;
import ch.post.it.evoting.verifier.report.ReportGenerator;

@Component
public class VerifierProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifierProcessor.class);

	private final ReportGenerator reportGenerator;
	private final ContestConfigurationReader contestConfigurationReader;
	private final ApplicationContext applicationContext;

	@Value("${inputDirectory:}")
	private String configurationInputDirectory;

	private List<Verification> verifications;

	@Autowired
	public VerifierProcessor(final ReportGenerator reportGenerator, final ContestConfigurationReader contestConfigurationReader,
			final ApplicationContext applicationContext) {
		this.reportGenerator = reportGenerator;
		this.contestConfigurationReader = contestConfigurationReader;
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	private void init() {
		if (configurationInputDirectory == null || configurationInputDirectory.length() == 0) {
			final String currentDirectory = System.getProperty("user.dir");
			final int lastBackslash = currentDirectory.lastIndexOf('\\');
			if (lastBackslash != -1) {
				configurationInputDirectory = currentDirectory.substring(0, lastBackslash);
			}

		}

		final Map<String, AbstractVerification> verificationBeans = applicationContext.getBeansOfType(AbstractVerification.class);
		LOGGER.debug("Found {} beans of type AbstractVerification.", verificationBeans.size());

		verifications = verificationBeans.values().stream()
				.map(AbstractVerification::getVerificationDefinition)
				.map(VerificationMapper.INSTANCE::map)
				.collect(Collectors.toList());

	}

	public List<Verification> getVerifications() {
		final List<Verification> result = new LinkedList<>(verifications);
		result.sort(Comparator.comparingInt(Verification::getBlockId).thenComparingInt(Verification::getVerificationId));

		return result;
	}

	public void resetExecution() {
		init();
	}

	public byte[] generatePdf(Language language) {
		final String contestName = contestConfigurationReader.getContestName(this.getConfiguration().getInputDirectory(), language);
		final var contestDate = contestConfigurationReader.getContestDate(this.getConfiguration().getInputDirectory());

		return reportGenerator.generate(contestName, contestDate, this.getVerifications(), language);
	}

	public Configuration getConfiguration() {
		final var result = new Configuration();
		result.setInputDirectory(configurationInputDirectory);

		return result;
	}

	public void setConfiguration(
			@NotNull
			final Configuration configuration) {
		this.configurationInputDirectory = configuration.getInputDirectory();
	}
}
