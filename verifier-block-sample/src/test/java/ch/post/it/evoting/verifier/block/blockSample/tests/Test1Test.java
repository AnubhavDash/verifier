package ch.post.it.evoting.verifier.block.blockSample.tests;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class Test1Test {

    @Test
    public void executeTest() {
        new Test1().executeTest(new File("c:\\temp"));
    }
}