/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.common.block.tools;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Class XmlMapper.
 * This class allow to convert XML file into java object.
 * its called XMLMapper without camel case
 * not to be confused with the XmlMapper jackson object
 * @author lalandret
 * @version $$Revision$$
 */
public class XMLMapper {
    public static <T> T mapFromXml(File inputDirectory, String filename, Class<T> targetClazz) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        File[] file = inputDirectory.listFiles((dir, name) -> name.endsWith(filename));
        if (file.length == 0) {
            throw new FileNotFoundException(filename);
        } else if (file.length > 1) {
            throw new InvalidParameterException("more than one file found, filename is not specific enough");
        } else {
            return xmlMapper.readValue(file[0], targetClazz);
        }
    }
}
