package ch.post.it.evoting.verifier.controller;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.post.it.evoting.verifier.common.TestTrait;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

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
    public void test_ping_returnsTrue() {
        assertEquals(true, controller.ping());
    }

    @Test
    public void test_process_callsProcessWithTraits() throws AlreadyStartedException {
        controller.process(TestTrait.PreDecryption.toString());
        Set<TestTrait> arguments = new HashSet<TestTrait>();
        arguments.add(TestTrait.PreDecryption);
        verify(processorMock).processTests(arguments);
    }

    @Test
    public void test_process_callsProcessWithoutTraits() throws AlreadyStartedException {
        controller.process(null);
        verify(processorMock).processTests( ArgumentMatchers.isNull());
    }

}
