/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test27Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

    @Test
    public void executeTestNOKNotFile() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/NOK-NOTFILE").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}