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
package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.report.ReportGenerator;
import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    default Report map(Report result, List<ch.post.it.evoting.verifier.dto.Test> testsList, Language lang) {
        List<Block> blockList = testsList.stream()
                .map(t -> t.getBlockId())
                .distinct()
                .sorted()
                .map(id -> {
                    Block block = new Block();
                    block.setTitle((TranslationHelper.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.block.title", lang.getLocale())) + id );
                    block.setDescription((TranslationHelper.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.block"+ id +".description", lang.getLocale())));
                    block.setTests(testsList.stream()
                            .filter(t -> t.getBlockId() == id)
                            .sorted(Comparator.comparingInt(Test::getTestId))
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
            @Mapping(target = "id", source = "test.testId"),
            @Mapping(target = "category", source = "test.category"),
            @Mapping(target = "description", expression = "java( test.getDescription().get(lang) )"),
            @Mapping(target = "status", source = "test.status"),
            @Mapping(target = "message", expression = "java( getMessage(test, lang) )")
    })
    ch.post.it.evoting.verifier.report.model.Test map(ch.post.it.evoting.verifier.dto.Test test, Language lang);

    default String getMessage(ch.post.it.evoting.verifier.dto.Test t, Language l) {
        if (t.getMessage() != null) {
            return t.getMessage().get(l);
        } else {
            return "";
        }
    }

    default String getLabel(String key, Language l) {
        return TranslationHelper.getFromResourceBundle(ReportGenerator.MESSAGE_BUNDLE_NAME, "report.test." + key, l.getLocale());
    }
}