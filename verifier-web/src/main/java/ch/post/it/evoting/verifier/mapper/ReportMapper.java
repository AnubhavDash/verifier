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
package ch.post.it.evoting.verifier.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.report.ReportGenerator;
import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;

@Mapper
public interface ReportMapper {

	ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

	default Report map(Report result, List<Verification> testsList, Language lang) {
		List<Block> blockList = testsList.stream()
				.map(t -> t.getBlockId())
				.distinct()
				.sorted()
				.map(id -> {
					Block block = new Block();
					block.setTitle(
							(TranslationHelper.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.block.title", lang.getLocale()))
									+ id);
					block.setDescription((TranslationHelper
							.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.block" + id + ".description", lang.getLocale())));
					block.setTests(testsList.stream()
							.filter(t -> t.getBlockId() == id)
							.sorted(Comparator.comparingInt(Verification::getVerificationId))
							.map(t -> map(t, lang))
							.collect(Collectors.toList()));
					return block;
				}).collect(Collectors.toList());

		result.setBlocksResults(blockList);
		return result;
	}

	@Mappings({
			@Mapping(target = "testIdLabel", expression = "java( getLabel(\"id\", lang) )"),
			@Mapping(target = "testNameLabel", expression = "java( getLabel(\"name\", lang) )"),
			@Mapping(target = "testCategoryLabel", expression = "java( getLabel(\"category\", lang) )"),
			@Mapping(target = "testDescriptionLabel", expression = "java( getLabel(\"description\", lang) )"),
			@Mapping(target = "testStatusLabel", expression = "java( getLabel(\"status\", lang) )"),
			@Mapping(target = "testMessageLabel", expression = "java( getLabel(\"message\", lang) )"),
			@Mapping(target = "id", source = "verification.verificationId"),
			@Mapping(target = "category", source = "verification.category"),
			@Mapping(target = "description", expression = "java( verification.getDescription().get(lang) )"),
			@Mapping(target = "status", source = "verification.status"),
			@Mapping(target = "message", expression = "java( getMessage(verification, lang) )")
	})
	ch.post.it.evoting.verifier.report.model.Test map(Verification verification, Language lang);

	default String getMessage(Verification v, Language l) {
		if (v.getMessage() != null) {
			return v.getMessage().get(l);
		} else {
			return "";
		}
	}

	default String getLabel(String key, Language l) {
		return TranslationHelper.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.test." + key, l.getLocale());
	}
}
