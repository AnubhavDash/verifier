package ch.post.it.evoting.verifier.controller;

import java.net.MalformedURLException;
import java.util.*;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.TestTrait;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.dto.Status;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestApiTest {

    private VerifierController controller;
    private VerifierProcessor processorMock;

    @Before
    public void setup()  {
        processorMock = mock(VerifierProcessor.class);
        controller = new VerifierController(processorMock);
    }

    @Test
    public void ping_returnsTrue() {
        assertEquals(true, controller.ping());
    }

    @Test
    public void tests_process_callsProcessWithTraits() throws AlreadyStartedException {
        controller.process(TestTrait.PreDecryption.toString());
        Set<TestTrait> arguments = new HashSet<TestTrait>();
        arguments.add(TestTrait.PreDecryption);
        verify(processorMock).processTests(arguments);
    }

    @Test
    public void tests_process_callsProcessWithoutTraits() throws AlreadyStartedException {
        controller.process(null);
        verify(processorMock).processTests( ArgumentMatchers.isNull());
    }

    @Test
    public void pdf_usesGerman()  {
        controller.generatePdf( Locale.GERMAN);
        verify(processorMock).generatePdf(Language.DE);
    }

    @Test
    public void pdf_usesFrench()  {
        controller.generatePdf( Locale.FRENCH);
        verify(processorMock).generatePdf(Language.FR);
    }

    @Test
    public void tests_get_callsGetTestStatus()  {
        controller.getTestStatus();
        verify(processorMock, atLeast(1)).getTestStatus();
    }

    @Test
    public void configurationInputDirectory_callsSetConfiguration()  {
        Configuration config = new Configuration();
        controller.setConfigurationInputDirectory(config);
        verify(processorMock).setConfiguration(config);
    }

    @Test
    public void configurationInputDirectory_callsGetConfiguration()  {
        controller.getConfiguration();
        verify(processorMock).getConfiguration();
    }

}
