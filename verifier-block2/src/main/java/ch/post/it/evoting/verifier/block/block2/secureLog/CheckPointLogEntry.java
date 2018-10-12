package ch.post.it.evoting.verifier.block.block2.secureLog;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.SecureLogOrigin;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public class CheckPointLogEntry extends SecureLogEntry {

    @Override
    protected void deserialize(String line) throws IOException {
        SecureLogOrigin slo = Deserializer.fromJson(line.getBytes(), SecureLogOrigin.class);
        setHost(slo.getResult().getHost());
        setIndex(slo.getResult().getIndex());
        setMetadata(getMetadataFromRaw(slo.getResult().getRaw()));

    }

}
