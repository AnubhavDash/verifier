/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.common.block.tools;

/**
 * Class CsvReader.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by aellenn on 29.03.2017.
 */
public class CsvReader<T> {
    private Function<String[], T> mapper;
    private Stream<String> stream;
    private boolean hasHeader = true;
    private String separator;

    public CsvReader(String filename, Charset charset, boolean hasHeader, String separator, Function<String[], T> mapper) throws IOException {
        this.mapper = mapper;
        stream = Files.lines(Paths.get(filename), charset);
        this.hasHeader = hasHeader;
        this.separator = separator;
    }

    public Iterable<T> process() {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                //skip the first line (header)
                //split with ; character
                //map to object with given Function
                return stream.skip(hasHeader ? 1 : 0).map(l -> l.split(separator)).map(mapper).iterator();
            }
        };
    }
}
