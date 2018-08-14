package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.util.TestDefinitionTools;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {TestDefinitionTools.class})
public interface TestExecutionStatusMapper {
    TestExecutionStatusMapper INSTANCE = Mappers.getMapper(TestExecutionStatusMapper.class);

    @Mappings({
            @Mapping(target = "id", expression = "java( TestDefinitionTools.computeUniqueKey(testDefinition) )"),
            @Mapping(target = "testId", source = "id"),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "message", ignore = true),
    })
    Test map(TestDefinition testDefinition);

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "message", source = "message"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "testId", ignore = true),
            @Mapping(target = "blockId", ignore = true),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "description", ignore = true),
    })
    void update(@MappingTarget Test testExecutionStatus, TestResult testResult);
}
