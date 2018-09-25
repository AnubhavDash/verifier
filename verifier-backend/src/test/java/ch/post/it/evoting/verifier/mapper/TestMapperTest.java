/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.report.pojo.Test;
import org.junit.Assert;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

public class TestMapperTest {

    private Test test;
    private ch.post.it.evoting.verifier.dto.Test testDto;

    @Before
    public void init(){
        testDto = new ch.post.it.evoting.verifier.dto.Test();
        testDto.setBlockId(1);
        testDto.setTestId(1);
        testDto.setId("1-1");
        testDto.setName("is prime de p");
        testDto.setCategory(Category.INTEGRITY);
        testDto.setStatus(Status.NOK);
        Map<Language, String> description = new HashMap<>();
        description.put(Language.FR, "desc in french");
        description.put(Language.DE, "desc in deutch");
        testDto.setDescription(description);
        Map<Language, String> message = new HashMap<>();
        message.put(Language.FR, "the test failed (french)");
        message.put(Language.DE, "the test failed (deutch)");
        testDto.setMessage(message);
    }

    @org.junit.Test
    public void map() {
        // map in french
        Test result = TestReportMapper.INSTANCE.map(testDto, Language.FR);
        Assert.assertEquals("id mapping failed", "1" , result.getId() );
        Assert.assertEquals("name mapping failed", "is prime de p" , result.getName() );
        Assert.assertEquals("description mapping failed", "desc in french" , result.getDescription() );
        Assert.assertEquals("category mapping failed", "INTEGRITY" , result.getCategory() );
        Assert.assertEquals("message mapping failed", "the test failed (french)" , result.getMessage() );

        // map in deutch
        result = TestReportMapper.INSTANCE.map(testDto, Language.DE);
        Assert.assertEquals("id mapping failed", "1" , result.getId() );
        Assert.assertEquals("name mapping failed", "is prime de p" , result.getName() );
        Assert.assertEquals("description mapping failed", "desc in deutch" , result.getDescription() );
        Assert.assertEquals("category mapping failed", "INTEGRITY" , result.getCategory() );
        Assert.assertEquals("message mapping failed", "the test failed (deutch)" , result.getMessage() );

    }
}