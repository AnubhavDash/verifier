/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
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
