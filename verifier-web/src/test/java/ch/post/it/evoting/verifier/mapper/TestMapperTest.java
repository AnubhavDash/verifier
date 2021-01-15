/*
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
