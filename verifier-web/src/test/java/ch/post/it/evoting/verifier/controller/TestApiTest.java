/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.controller;

import java.util.*;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.TestTrait;
import ch.post.it.evoting.verifier.dto.Configuration;
import ch.post.it.evoting.verifier.processor.AlreadyStartedException;
import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import org.junit.Before;
import org.junit.Test;
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
