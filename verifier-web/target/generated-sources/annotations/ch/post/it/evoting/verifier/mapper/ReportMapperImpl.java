package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.dto.Test;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-10-24T13:55:11+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class ReportMapperImpl implements ReportMapper {

    @Override
    public ch.post.it.evoting.verifier.report.pojo.Test map(Test test, Language lang) {
        if ( test == null && lang == null ) {
            return null;
        }

        ch.post.it.evoting.verifier.report.pojo.Test test1 = new ch.post.it.evoting.verifier.report.pojo.Test();

        if ( test != null ) {
            test1.setId( String.valueOf( test.getTestId() ) );
            if ( test.getCategory() != null ) {
                test1.setCategory( test.getCategory().name() );
            }
            if ( test.getStatus() != null ) {
                test1.setStatus( test.getStatus().name() );
            }
            test1.setName( test.getName() );
        }
        test1.setTestDescriptionLabel( getLabel("description", lang) );
        test1.setTestNameLabel( getLabel("name", lang) );
        test1.setTestCategoryLabel( getLabel("kategorie", lang) );
        test1.setTestMessageLabel( getLabel("message", lang) );
        test1.setDescription( test.getDescription().get(lang) );
        test1.setMessage( getMessage(test, lang) );
        test1.setTestStatusLabel( getLabel("status", lang) );
        test1.setTestIdLabel( getLabel("id", lang) );

        return test1;
    }
}
