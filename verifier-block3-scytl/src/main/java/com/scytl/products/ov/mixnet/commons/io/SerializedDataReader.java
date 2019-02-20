/**
 * @author afries
 * @date   Jul 15, 2015 10:32:21 AM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public abstract class SerializedDataReader {

    protected static void validateFileIsAccessible(final File fileToCheck) {

        if (!fileToCheck.exists()) {
            throw new RuntimeException("The received path does not exist: " + fileToCheck.toString());
        }

        if (!fileToCheck.canRead()) {
            throw new RuntimeException(
                "The received path does not have the right permissions: " + fileToCheck.toString());
        }
    }

    protected static String readLineAndConfirmNotNull(final BufferedReader fileReader, final String pathAsString)
            throws IOException {

        final String line = fileReader.readLine();

        if (line == null) {
            throw new RuntimeException("The line that was read from file was null. File was : " + pathAsString);
        } else {
            return line;
        }
    }
}
