/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.SecureLogOrigin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
public abstract class SecureLogEntry {

    private Boolean preview;
    private String host;
    private String raw;
    private String source;
    private SecureLogMetadata metadata;

    public static SecureLogEntry from(String line) throws IOException {
        SecureLogEntry result;
        if (line.contains("lastrow")) {
            result = new LastRowEntry();
        } else if (line.contains("New Secret Key generated")) {
            result = new CheckPointLogEntry();
            result.deserialize(line);
        } else {
            result = new RegularLogEntry();
            result.deserialize(line);
        }
        return result;
    }

    protected void deserialize(String line) throws IOException {
        SecureLogOrigin slo = Deserializer.fromJson(line.getBytes(), SecureLogOrigin.class);

        setPreview(slo.getPreview());
        if (slo.getResult() != null) {
            setSource(slo.getResult().getSource());
            setHost(slo.getResult().getHost());
            setRaw(getRawWithoutMetadata(slo.getResult().getRaw()));
            setMetadata(getMetadataFromRaw(slo.getResult().getRaw()));
        }
    }

    protected String getRawWithoutMetadata(String raw) {
        String result = null;
        if (raw != null && !raw.isEmpty()) {
            Matcher matcher = Pattern.compile("(.*?)(\\{\\*.*)?$").matcher(raw);
            if (matcher.matches()) {
                result = matcher.group(1);

                //replace the last character and with a line feed.
                if (result.length() > 0) {
                    result = result.substring(0, result.length() - 1) + "\n";
                }
            }
        }
        return result;
    }

    protected SecureLogMetadata getMetadataFromRaw(String raw) {
        SecureLogMetadata metadata = new SecureLogMetadata();
        if (raw != null && !raw.isEmpty()) {
            String metadataString = getMetadataString(raw);
            if (metadataString != null && !metadataString.isEmpty()) {
                metadata.setSg(getMetadataValue(metadataString, "SG"));
                metadata.setLsk(getMetadataValue(metadataString, "LSK"));
                metadata.setEsk(getMetadataValue(metadataString, "ESK"));
                metadata.setHmac(getMetadataValue(metadataString, "HMAC"));
                metadata.setPhmac(getMetadataValue(metadataString, "PHMAC"));
                metadata.setLs(getMetadataValue(metadataString, "LS"));
                metadata.setTl(getMetadataValue(metadataString, "TL"));
                metadata.setTs(getMetadataValue(metadataString, "TS"));
            }
        }
        return metadata;
    }

    private String getMetadataString(String raw) {
        String result = null;
        if (raw != null && !raw.isEmpty()) {
            Matcher matcher = Pattern.compile(".*\\{\\*(.*)\\*\\}.*").matcher(raw);
            if (matcher.matches()) {
                result = matcher.group(1);
            }
        }
        return result;
    }

    private String getMetadataValue(String metadataString, String key) {
        return Arrays.stream(metadataString.split(","))
                .filter(val -> val.startsWith(key.toUpperCase() + "::"))
                .map(val -> val.split("::"))
                .filter(tab -> tab.length == 2)
                .map(val -> val[1])
                .findFirst()
                .orElse(null);
    }
}
