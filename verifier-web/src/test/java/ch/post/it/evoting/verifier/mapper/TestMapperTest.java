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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.report.model.Test;

class TestMapperTest {

	private Verification verificationDto;

	@BeforeEach
	void init() {
		verificationDto = new Verification();
		verificationDto.setBlockId(1);
		verificationDto.setVerificationId(1);
		verificationDto.setId("1-1");
		verificationDto.setName("is prime de p");
		verificationDto.setCategory(Category.INTEGRITY);
		verificationDto.setStatus(Status.NOK);
		Map<Language, String> description = new HashMap<>();
		description.put(Language.FR, "desc in french");
		description.put(Language.DE, "desc in deutch");
		verificationDto.setDescription(description);
		Map<Language, String> message = new HashMap<>();
		message.put(Language.FR, "the test failed (french)");
		message.put(Language.DE, "the test failed (deutch)");
		verificationDto.setMessage(message);
	}

	@org.junit.jupiter.api.Test
	void map() {
		// map in french
		Test result = ReportMapper.INSTANCE.map(verificationDto, Language.FR);
		assertEquals("1", result.getId(), "id mapping failed");
		assertEquals("is prime de p", result.getName(), "name mapping failed");
		assertEquals("desc in french", result.getDescription(), "description mapping failed");
		assertEquals("INTEGRITY", result.getCategory(), "category mapping failed");
		assertEquals("the test failed (french)", result.getMessage(), "message mapping failed");

		// map in deutch
		result = ReportMapper.INSTANCE.map(verificationDto, Language.DE);
		assertEquals("1", result.getId(), "id mapping failed");
		assertEquals("is prime de p", result.getName(), "name mapping failed");
		assertEquals("desc in deutch", result.getDescription(), "description mapping failed");
		assertEquals("INTEGRITY", result.getCategory(), "category mapping failed");
		assertEquals("the test failed (deutch)", result.getMessage(), "message mapping failed");

	}
}
