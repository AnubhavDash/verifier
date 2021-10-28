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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.VerifierBlock;
import ch.post.it.evoting.verifier.contest.ContestConfigurationReader;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.mapper.VerificationExecutionStatusMapper;
import ch.post.it.evoting.verifier.report.ReportGenerator;

@Component
@ComponentScan(basePackages = "${verifier.blocks.componentScan}", useDefaultFilters = false, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { VerifierBlock.class }) })
public class VerifierProcessor {
	private static final Logger log = Logger.getLogger(VerifierProcessor.class);
	private final ReportGenerator reportGenerator;
	private final ContestConfigurationReader contestConfigurationReader;
	@Value("${inputDirectory}")
	private String configurationInputDirectory;
	private Map<String, Verification> executionStatus;
	@Autowired
	private List<VerifierBlock> blocks;
	private List<ProcessListener> listeners;
	private boolean processed;

	@Autowired
	public VerifierProcessor(ReportGenerator reportGenerator, ContestConfigurationReader contestConfigurationReader) {
		this.reportGenerator = reportGenerator;
		this.contestConfigurationReader = contestConfigurationReader;
	}

	@PostConstruct
	private void init() {
		if (configurationInputDirectory == null || configurationInputDirectory.length() == 0) {
			String currentDirectory = System.getProperty("user.dir");
			int lastBackslash = currentDirectory.lastIndexOf('\\');
			if (lastBackslash != -1) {
				configurationInputDirectory = currentDirectory.substring(0, lastBackslash);
			}

		}
		listeners = new LinkedList<>();
		processed = false;

		executionStatus = blocks.parallelStream().flatMap(VerifierBlock::getVerifications)
				.collect(Collectors.toMap(VerificationDefinition::computeUniqueKey, VerificationExecutionStatusMapper.INSTANCE::map));
	}

	public List<Verification> getVerificationStatus() {
		List<Verification> result = new LinkedList<>(executionStatus.values());
		result.sort(Comparator.comparingInt(Verification::getBlockId).thenComparingInt(Verification::getVerificationId));
		return result;
	}

	public void processVerifications(Set<VerificationTrait> options) throws AlreadyStartedException {
		if (!processed) {
			processed = true;

			final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
			executor.execute(() -> {
				ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
				forkJoinPool.submit(() -> {
					final Path inputDirectoryPath = Paths.get(configurationInputDirectory);
					blocks.stream()
							.map(block -> block.process(inputDirectoryPath, options).parallel())
							.reduce(Stream.empty(), Stream::concat)
							.parallel()
							.forEach(v -> {
								log.debug(String.format("Verification '%02d-%02d' performed on Thread '%s' %s",
										v.getVerificationDefinition().getBlockId(), v.getVerificationDefinition().getId(),
										Thread.currentThread().getName(), v.getStatus()));
								verificationProcessed(v);
							});
				});
			});
		} else {
			throw new AlreadyStartedException();
		}
	}

	private void verificationProcessed(VerificationResult verificationResult) {
		Verification verificationExecutionStatus = executionStatus.get(verificationResult.getVerificationDefinition().computeUniqueKey());
		VerificationExecutionStatusMapper.INSTANCE.update(verificationExecutionStatus, verificationResult);

		listeners.forEach(l -> l.verificationProcessed(verificationExecutionStatus));
	}

	public void registerProcessListener(ProcessListener listener) {
		listeners.add(listener);
	}

	public void resetExecution() {
		LinkedList<ProcessListener> copy = new LinkedList<>(this.listeners);
		init();
		this.listeners = copy;
	}

	public byte[] generatePdf(Language language) {
		String contestName = contestConfigurationReader.getContestName(this.getConfiguration().getInputDirectory(), language);
		Date contestDate = contestConfigurationReader.getContestDate(this.getConfiguration().getInputDirectory());
		return reportGenerator.generate(contestName, contestDate, this.getVerificationStatus(), language);
	}

	public Configuration getConfiguration() {
		Configuration result = new Configuration();
		result.setInputDirectory(configurationInputDirectory);
		return result;
	}

	public void setConfiguration(@NotNull Configuration configuration) {
		this.configurationInputDirectory = configuration.getInputDirectory();
	}
}
