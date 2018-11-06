package ch.post.it.evoting.verifier.block.block2.secureLog;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.SecureLogOrigin;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class RegularLogEntry extends SecureLogEntry {

    @Override
    protected void deserialize(String line) throws IOException {
        SecureLogOrigin slo = Deserializer.fromJson(line.getBytes(), SecureLogOrigin.class);
        setPreview(slo.getPreview());
        setHost(slo.getResult().getEv().substring(0, slo.getResult().getEv().indexOf('|')));
        setRaw(getCleanedRawFromRaw(slo.getResult().getEv()));
        setMetadata(getMetadataFromRaw(slo.getResult().getEv()));
    }


}
