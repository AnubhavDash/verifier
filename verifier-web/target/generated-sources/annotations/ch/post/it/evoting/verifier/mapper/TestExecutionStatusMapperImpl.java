package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.util.TestDefinitionTools;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-10-24T13:55:11+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class TestExecutionStatusMapperImpl implements TestExecutionStatusMapper {

    @Override
    public Test map(TestDefinition testDefinition) {
        if ( testDefinition == null ) {
            return null;
        }

        Test test = new Test();

        test.setTestId( testDefinition.getId() );
        test.setBlockId( testDefinition.getBlockId() );
        test.setName( testDefinition.getName() );
        test.setCategory( testDefinition.getCategory() );
        Map<Language, String> map = testDefinition.getDescription();
        if ( map != null ) {
            test.setDescription( new HashMap<Language, String>( map ) );
        }
        else {
            test.setDescription( null );
        }

        test.setId( TestDefinitionTools.computeUniqueKey(testDefinition) );

        return test;
    }

    @Override
    public void update(Test testExecutionStatus, TestResult testResult) {
        if ( testResult == null ) {
            return;
        }

        if ( testExecutionStatus.getMessage() != null ) {
            Map<Language, String> map = testResult.getMessage();
            if ( map != null ) {
                testExecutionStatus.getMessage().clear();
                testExecutionStatus.getMessage().putAll( map );
            }
            else {
                testExecutionStatus.setMessage( null );
            }
        }
        else {
            Map<Language, String> map = testResult.getMessage();
            if ( map != null ) {
                testExecutionStatus.setMessage( new HashMap<Language, String>( map ) );
            }
        }
        testExecutionStatus.setStatus( testResult.getStatus() );
    }
}
