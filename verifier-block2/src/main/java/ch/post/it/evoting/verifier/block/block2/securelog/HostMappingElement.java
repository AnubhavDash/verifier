/*
 * Copyright 2019 by Swiss Post, Information Technology
 */

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
package ch.post.it.evoting.verifier.block.block2.securelog;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
public class HostMappingElement {
    private String hostname;
    private String cc;

    public final static Map<String, String> loadHostMapping(File inputDirectory) throws IOException {
        File mapping = PathHelper.getFile(inputDirectory.toPath().resolve(Block2VerificationSuite.PATH_SECURE_LOGS).toFile(), "mapping_cc_hosts.csv");
        Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", map);
        return StreamSupport.stream(iterable.spliterator(), false)
                .skip(1)
                .collect(Collectors.toMap(HostMappingElement::getHostname, HostMappingElement::getCc));
    }

    private final static Function<String[], HostMappingElement> map = array -> {
        if (array == null || array.length != 2) {
            throw new IllegalArgumentException("Wrong array input format");
        }
        HostMappingElement hm = new HostMappingElement();
        hm.setHostname(array[0]);
        hm.setCc(array[1]);
        return hm;
    };
}
