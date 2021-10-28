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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.dto.Verification;

@Mapper
public interface VerificationExecutionStatusMapper {
	VerificationExecutionStatusMapper INSTANCE = Mappers.getMapper(VerificationExecutionStatusMapper.class);

	@Mappings({
			@Mapping(target = "id", expression = "java( verificationDefinition.computeUniqueKey() )"),
			@Mapping(target = "verificationId", source = "id"),
			@Mapping(target = "status", ignore = true),
			@Mapping(target = "message", ignore = true),
	})
	Verification map(VerificationDefinition verificationDefinition);

	@Mappings({
			@Mapping(target = "status", source = "status"),
			@Mapping(target = "message", source = "message"),
			@Mapping(target = "id", ignore = true),
			@Mapping(target = "verificationId", ignore = true),
			@Mapping(target = "blockId", ignore = true),
			@Mapping(target = "name", ignore = true),
			@Mapping(target = "category", ignore = true),
			@Mapping(target = "description", ignore = true),
	})
	void update(
			@MappingTarget
					Verification verificationExecutionStatus, VerificationResult verificationResult);
}
