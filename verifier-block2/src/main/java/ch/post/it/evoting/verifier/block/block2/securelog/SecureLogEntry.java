package ch.post.it.evoting.verifier.block.block2.securelog;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.SecureLogOrigin;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public abstract class SecureLogEntry {

    private Boolean preview;
    private String host;
    private String raw;
    private String source;
    private SecureLogMetadata metadata;

    protected void deserialize(String line) throws IOException {
        SecureLogOrigin slo = Deserializer.fromJson(line.getBytes(), SecureLogOrigin.class);

        setPreview(slo.getPreview());
        setSource(slo.getResult().getSource());
        setHost(slo.getResult().getHost()/*.getRaw().substring(0, slo.getResult().getRaw().indexOf('|'))*/);
        setRaw(getCleanedRawFromRaw(slo.getResult().getRaw()/*.substring(slo.getResult().getRaw().indexOf('|') + 1)*/));
        setMetadata(getMetadataFromRaw(slo.getResult().getRaw()));
    }

    public static SecureLogEntry from(String line) throws IOException {
        SecureLogEntry result;
        if (line.contains("New Secret Key generated")) {
            result = new CheckPointLogEntry();
        } else {
            result = new RegularLogEntry();
        }
        result.deserialize(line);
        return result;
    }

    protected String getCleanedRawFromRaw(String raw) {
        String result = null;
        if (raw != null && !raw.isEmpty()) {
            String objInsideRaw = getObjectInsideRaw(raw) + "*}";
            result = raw.replace(objInsideRaw, "");
            result = result.substring(0, result.length() - 1) + "\n";
        }
        return result;
    }

    protected SecureLogMetadata getMetadataFromRaw(String raw) {
        SecureLogMetadata metadata = new SecureLogMetadata();
        if (raw != null && !raw.isEmpty()) {
            String objInsideRaw = getObjectInsideRaw(raw);
            if (objInsideRaw != null && !objInsideRaw.isEmpty()) {
                metadata.setSg(getSignFromObjInRaw(objInsideRaw));
                metadata.setLsk(getLskFromObjInRaw(objInsideRaw));
                metadata.setEsk(getEskFromObjInRaw(objInsideRaw));
                metadata.setHmac(getHmacFromObjInRaw(objInsideRaw));
                metadata.setPhmac(getPhmacFromObjInRaw(objInsideRaw));
                metadata.setLs(getLsFromObjInRaw(objInsideRaw));
                metadata.setTl(getTlFromObjInRaw(objInsideRaw));
                metadata.setTs(getTsFromObjInRaw(objInsideRaw));
            }
        }
        return metadata;
    }


    private String getSignFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> sigPart = Arrays.asList(split).stream().filter(str -> str.contains("SG::")).findFirst();
            result = sigPart.isPresent() ? getValueFromKeyValueString(sigPart.get()) : null;
        }
        return result;
    }

    private String getLskFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> lsk = Arrays.asList(split).stream().filter(str -> str.contains("LSK::")).findFirst();
            result = lsk.isPresent() ? getValueFromKeyValueString(lsk.get()) : null;
        }
        return result;
    }

    private String getEskFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> esk = Arrays.asList(split).stream().filter(str -> str.contains("ESK::")).findFirst();
            result = esk.isPresent() ? getValueFromKeyValueString(esk.get()) : null;
        }
        return result;
    }

    private String getHmacFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> hmacPart = Arrays.asList(split).stream().filter(str -> str.startsWith("HMAC::")).findFirst();
            result = hmacPart.isPresent() ? getValueFromKeyValueString(hmacPart.get()) : null;
        }
        return result;
    }

    private String getPhmacFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> phmacPart = Arrays.asList(split).stream().filter(str -> str.contains("PHMAC::")).findFirst();
            result = phmacPart.isPresent() ? getValueFromKeyValueString(phmacPart.get()) : null;
        }
        return result;
    }

    private String getLsFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> ls = Arrays.asList(split).stream().filter(str -> str.contains("LS::")).findFirst();
            result = ls.isPresent() ? getValueFromKeyValueString(ls.get()) : null;
        }
        return result;
    }

    private String getTlFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> tl = Arrays.asList(split).stream().filter(str -> str.contains("TL::")).findFirst();
            result = tl.isPresent() ? getValueFromKeyValueString(tl.get()) : null;
        }
        return result;
    }

    private String getTsFromObjInRaw(String objInsideRaw) {
        String result = null;
        if (objInsideRaw != null) {
            String[] split = objInsideRaw.split(",");
            Optional<String> ts = Arrays.asList(split).stream().filter(str -> str.contains("TS::")).findFirst();
            result = ts.isPresent() ? getValueFromKeyValueString(ts.get()) : null;
        }
        return result;
    }

    private String getObjectInsideRaw(String raw) {
        int startBracket = raw.indexOf("{*", 0);
        if (startBracket == -1) {
            return null;
        }
        int endBracket = raw.indexOf("*}", startBracket);
        return raw.substring(startBracket, endBracket);
    }

    private String getValueFromKeyValueString(String str) {
        int firstColon = str.indexOf("::");
        return str.substring(firstColon + 2);
    }

    @Override
    public String toString() {
        return "SecureLogEntry{" +
                "host='" + host + '\'' +
                ", raw='" + raw + '\'' +
                '}';
    }
}
