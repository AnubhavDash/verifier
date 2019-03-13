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
package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class Test29Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult result = new Test29().executeTest(new File(getClass().getResource("/Test29/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Ignore
    @Test
    public void executeTestNOK() {
        TestResult result = new Test29().executeTest(new File(getClass().getResource("/Test29/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}