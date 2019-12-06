/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public abstract class SecureLogEntry {

    private String host;
    private String raw;
    private String source;
    private SecureLogMetadata metadata;

    public static SecureLogEntry from(String line, String hostName, String filename) {
        SecureLogEntry result;
        if (line.contains("lastrow")) {
            result = new LastRowEntry();
        } else if (line.contains("New Secret Key generated")) {
            result = new CheckPointLogEntry();
        } else {
            result = new RegularLogEntry();
        }
        result.setHost(hostName);
        result.setSource(filename);
        result.deserialize(line);
        return result;
    }

    private final static String METADATA_START_TAG = " {*";
    private final static String METADATA_END_TAG = "*}";

    protected void deserialize(String line) {
        if (StringUtils.isNotEmpty(line)) {
            //Using substring and not PatternMatching because of performance considerations
            int endOfRawIndex = line.indexOf(METADATA_START_TAG);
            this.setRaw(line.substring(0, endOfRawIndex) + "\n");

            SecureLogMetadata metadata = new SecureLogMetadata();
            String metadataString = line.substring(endOfRawIndex + METADATA_START_TAG.length(), line.indexOf(METADATA_END_TAG));
            if (metadataString != null && !metadataString.isEmpty()) {
                Map<String, String> metaDataValues = getMetadataValues(metadataString);
                metadata.setSg(metaDataValues.get("SG"));
                metadata.setLsk(metaDataValues.get("LSK"));
                metadata.setEsk(metaDataValues.get("ESK"));
                metadata.setHmac(metaDataValues.get("HMAC"));
                metadata.setPhmac(metaDataValues.get("PHMAC"));
                metadata.setLs(metaDataValues.get("LS"));
                metadata.setTl(metaDataValues.get("TL"));
                metadata.setTs(metaDataValues.get("TS"));
            }
            this.setMetadata(metadata);
        }
    }

    private Map<String, String> getMetadataValues(String metadataString) {
        return Arrays.stream(metadataString.split(","))
                .map(val -> val.split("::"))
                .filter(tab -> tab.length == 2)
                .collect(Collectors.toMap(tab -> tab[0], tab -> tab[1]));
    }


    public final static Function<File, Flux<SecureLogEntry>> loadLogDirectory = logDir -> {
        try {
            return Flux.fromArray(PathHelper.getFiles(logDir, ".*\\.log"))
                    .sort(Comparator.comparing(File::getName))
                    .concatMap(SecureLogEntry.loadFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("directory without log files :" + logDir.getName(), e);
        }
    };

    public static Flux<RegularLogEntry> loadRegularLogs(File inputDirectory, Pattern pattern) {
        return Flux.fromArray(PathHelper.listDirectories(inputDirectory.toPath().resolve(Block2VerificationSuite.PATH_SECURE_LOGS)))
                .onErrorStop()
                .flatMap(hostDir -> Flux.fromArray(PathHelper.listDirectories(hostDir.toPath())))
                .flatMap(instanceDir -> Flux.fromArray(PathHelper.listDirectories(instanceDir.toPath())))
                .flatMap(SecureLogEntry.loadLogDirectory)
                .switchIfEmpty(Flux.<SecureLogEntry>empty().doOnComplete(() -> {throw new RuntimeException("No secureLog found");}))
                .filter(s1 -> s1 instanceof RegularLogEntry)
                .cast(RegularLogEntry.class)
                .filter(s1 -> pattern.matcher(s1.getRaw()).find());
    }

    private static Function<File, Publisher<SecureLogEntry>> loadFile = file -> {
        try {
            String hostName = file.getParentFile().getParentFile().getParentFile().getName();
            return Flux.fromStream(Files.lines(file.toPath()))
                    .map(line -> SecureLogEntry.from(line, hostName, file.getName()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file : " + file.getName(), e);
        }
    };
}
