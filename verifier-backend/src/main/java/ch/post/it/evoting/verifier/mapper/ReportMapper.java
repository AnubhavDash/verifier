package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.report.pojo.Block;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
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

    default Report map(List<ch.post.it.evoting.verifier.dto.Test> testsList, ReportMetadata metadata, Language lang) {
        Report result = new Report(metadata);

        List<Block> blockList = testsList.stream()
                .map(t -> t.getBlockId())
                .distinct()
                .sorted()
                .map(id -> {
                    Block block = new Block();
                    block.setTitre("Block " + id);
                    block.setDescription("Description du block " + id);
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
            @Mapping(target = "testIdLabel", expression = "java( getLabel(\"idLabel\", lang) )"),
            @Mapping(target = "testNameLabel", expression = "java( getLabel(\"nameLabel\", lang) )"),
            @Mapping(target = "testCategoryLabel", expression = "java( getLabel(\"kategorieLabel\", lang) )"),
            @Mapping(target = "testDescriptionLabel", expression = "java( getLabel(\"descriptionLabel\", lang) )"),
            @Mapping(target = "testStatusLabel", expression = "java( getLabel(\"statusLabel\", lang) )"),
            @Mapping(target = "testMessageLabel", expression = "java( getLabel(\"messageLabel\", lang) )"),
            @Mapping(target = "id", source = "test.testId"),
            @Mapping(target = "category", source = "test.category"),
            @Mapping(target = "description", expression = "java( test.getDescription().get(lang) )"),
            @Mapping(target = "status", source = "test.status"),
            @Mapping(target = "message", expression = "java( getMessage(test, lang) )")
    })
    ch.post.it.evoting.verifier.report.pojo.Test map(ch.post.it.evoting.verifier.dto.Test test, Language lang);

    default String getMessage(ch.post.it.evoting.verifier.dto.Test t, Language l) {
        if (t.getMessage() != null) {
            return t.getMessage().get(l);
        } else {
            return "";
        }
    }

    //TODO Labels should be different regarding the langiuage
    default String getLabel(String key, Language l) {
        String result = null;
        switch (key) {
            case "idLabel":
                result = "N°";
                break;
            case "nameLabel":
                result = "Name";
                break;
            case "kategorieLabel":
                result = "Kategorie";
                break;
            case "descriptionLabel":
                result = "Description";
                break;
            case "statusLabel":
                result = "Status";
                break;
            case "messageLabel":
                result = "";
                break;
        }
        return result;
    }
}