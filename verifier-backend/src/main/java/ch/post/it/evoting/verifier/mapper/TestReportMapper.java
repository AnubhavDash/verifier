/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.report.pojo.TestReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
@Mapper
public interface TestReportMapper {
    
    TestReportMapper INSTANCE = Mappers.getMapper(TestReportMapper.class);

    @Mappings({
            @Mapping(target = "id", expression = "java( Integer.toString(test.getTestId()) )"),
            @Mapping(target = "category", expression = "java( test.getCategory().toString() )"),
            @Mapping(target = "description", expression = "java( test.getDescription().get(lang) )"),
            @Mapping(target = "status", expression = "java( test.getStatus().toString() )"),
            @Mapping(target = "message", expression = "java( getMessage(test, lang) )")
    })
    TestReport map(Test test, Language lang);

    default String getMessage(Test t, Language l) {
        if (t.getMessage() != null) {
            return t.getMessage().get(l);
        } else {
            return null;
        }
    }


}
