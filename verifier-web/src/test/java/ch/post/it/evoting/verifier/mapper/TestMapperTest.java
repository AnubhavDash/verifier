/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.report.model.Test;
import org.junit.Assert;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

public class TestMapperTest {

    private Verification verificationDto;

    @Before
    public void init() {
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

    @org.junit.Test
    public void map() {
        // map in french
        Test result = ReportMapper.INSTANCE.map(verificationDto, Language.FR);
        Assert.assertEquals("id mapping failed", "1", result.getId());
        Assert.assertEquals("name mapping failed", "is prime de p", result.getName());
        Assert.assertEquals("description mapping failed", "desc in french", result.getDescription());
        Assert.assertEquals("category mapping failed", "INTEGRITY", result.getCategory());
        Assert.assertEquals("message mapping failed", "the test failed (french)", result.getMessage());

        // map in deutch
        result = ReportMapper.INSTANCE.map(verificationDto, Language.DE);
        Assert.assertEquals("id mapping failed", "1", result.getId());
        Assert.assertEquals("name mapping failed", "is prime de p", result.getName());
        Assert.assertEquals("description mapping failed", "desc in deutch", result.getDescription());
        Assert.assertEquals("category mapping failed", "INTEGRITY", result.getCategory());
        Assert.assertEquals("message mapping failed", "the test failed (deutch)", result.getMessage());

    }
}
