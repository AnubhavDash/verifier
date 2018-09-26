/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.report.pojo.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
@Mapper
public interface TestReportMapper {
    
    TestReportMapper INSTANCE = Mappers.getMapper(TestReportMapper.class);

    @Mappings({
            @Mapping(target = "testIdLabel", expression = "java( getLabel(\"idLabel\", lang) )"),
            @Mapping(target = "testNameLabel", expression = "java( getLabel(\"nameLabel\", lang) )"),
            @Mapping(target = "testCategoryLabel", expression = "java( getLabel(\"kategorieLabel\", lang) )"),
            @Mapping(target = "testDescriptionLabel", expression = "java( getLabel(\"descriptionLabel\", lang) )"),
            @Mapping(target = "testStatusLabel", expression = "java( getLabel(\"statusLabel\", lang) )"),
            @Mapping(target = "testMessageLabel", expression = "java( getLabel(\"messageLabel\", lang) )"),
            @Mapping(target = "id", expression = "java( Integer.toString(test.getTestId()) )"),
            @Mapping(target = "category", expression = "java( test.getCategory().toString() )"),
            @Mapping(target = "description", expression = "java( test.getDescription().get(lang) )"),
            @Mapping(target = "status", expression = "java( test.getStatus().toString() )"),
            @Mapping(target = "message", expression = "java( getMessage(test, lang) )")
    })
    Test map(ch.post.it.evoting.verifier.dto.Test test, Language lang);

    default String getMessage(ch.post.it.evoting.verifier.dto.Test t, Language l) {
        if (t.getMessage() != null) {
            return t.getMessage().get(l);
        } else {
            return null;
        }
    }

    //TODO Labels should be different regarding the langiuage
    default String getLabel(String key, Language l){
        String result = null;
        switch (key){
            case "idLabel": result = "N°"; break;
            case "nameLabel": result = "Name"; break;
            case "kategorieLabel": result = "Kategorie"; break;
            case "descriptionLabel": result = "Description"; break;
            case "statusLabel": result = "Status"; break;
            case "messageLabel": result = ""; break;
        }
        return result;
    }


}
