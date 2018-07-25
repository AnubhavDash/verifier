/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.common.block.tools;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Class JsonMapper.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class JsonMapper {
    public static <T> T mapFromJson(File inputDirectory, String filename, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        File[] file = inputDirectory.listFiles((dir, name) -> name.contains(filename));
        if (file.length == 0) {
            throw new FileNotFoundException(filename);
        } else if (file.length > 1) {
            throw new InvalidParameterException("more than one file found, filename is not specific enough");
        } else {
            return jsonMapper.readValue(file[0], targetClazz);
        }
    }
}
