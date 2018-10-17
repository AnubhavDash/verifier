package ch.post.it.evoting.verifier.block.block2.secureLog;

import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SecureLogBundle {
    private static final Logger LOGGER = Logger.getLogger(SecureLogBundle.class);

    private CheckPointLogEntry beginCheckPoint;
    private CheckPointLogEntry endCheckPoint;
    private List<RegularLogEntry> regularLogEntries = new ArrayList<>();

    public void copyTo(SecureLogBundle other) {
        other.setBeginCheckPoint(beginCheckPoint);
        other.setEndCheckPoint(endCheckPoint);
        other.regularLogEntries = new LinkedList<>(regularLogEntries);
    }

    public void setBeginCheckPoint(CheckPointLogEntry beginCheckPoint) {
        this.beginCheckPoint = beginCheckPoint;
    }

    public CheckPointLogEntry getBeginCheckPoint() {
        return beginCheckPoint;
    }

    public void setEndCheckPoint(CheckPointLogEntry endCheckPoint) {
        this.endCheckPoint = endCheckPoint;
    }

    public CheckPointLogEntry getEndCheckPoint() {
        return endCheckPoint;
    }

    public void addRegularLogEntry(RegularLogEntry regularLogEntry) {
        regularLogEntries.add(regularLogEntry);
    }

    public boolean hasRegularLogEntries() {
        return !regularLogEntries.isEmpty();
    }

    public boolean isComplete() {
        return this.endCheckPoint != null;
    }

    public void validate() throws SecureLogBundleValidationException {
        if (!this.isComplete() && this.hasRegularLogEntries()) {
            throw new SecureLogBundleValidationException("bundle is not finishing with a checkPoint", beginCheckPoint.getHost());
        }
        LOGGER.trace(String.format("Starting validation of Bundle{prev:%s, curr:%s, elementsCount:%s}", this.beginCheckPoint, this.endCheckPoint, this.regularLogEntries.size()));
        //byte[] beginHmac = validateStartCheckPoint();
        //byte[] lastHmac = validateRegularLogs(beginHmac);
        //validateEndCheckPoint(lastHmac);
    }

    private void validateEndCheckPoint(byte[] lastHmac) throws SecureLogBundleValidationException {
        if (!endCheckPoint.getMetadata().getPhmac().equals(lastHmac)) {
            throw new SecureLogBundleValidationException("End Checkpoint HMAC not valid", beginCheckPoint.getHost());
        }
    }

    private byte[] validateRegularLogs(byte[] beginCheckPointHmac) throws SecureLogBundleValidationException {
        byte[] lsk = Base64.decode(endCheckPoint.getMetadata().getLsk());
        byte[] previousHmac = beginCheckPointHmac;
        for (RegularLogEntry regularLogEntry : regularLogEntries) {
            byte[] text = concat(
                    previousHmac,
                    regularLogEntry.getRaw().getBytes(StandardCharsets.UTF_8));
            byte[] hmac = HmacGenerator.Hash(text, lsk);
            String hmacString = Base64.toBase64String(hmac);
            if (!hmacString.equals(regularLogEntry.getMetadata().getHmac())) {
                throw new SecureLogBundleValidationException("Regular log HMAC not valid", beginCheckPoint.getHost());
            }
            previousHmac = hmac;
        }
        return previousHmac;
    }

    private byte[] validateStartCheckPoint() throws SecureLogBundleValidationException {
        byte[] lsk = Base64.decode(endCheckPoint.getMetadata().getLsk());
        byte[] text = concat(
                Base64.decode(beginCheckPoint.getMetadata().getPhmac()),
                Base64.decode(beginCheckPoint.getMetadata().getLsk()),
                Base64.decode(beginCheckPoint.getMetadata().getEsk()),
                beginCheckPoint.getRaw().getBytes(StandardCharsets.UTF_8));
        byte[] hmac = HmacGenerator.Hash(text, lsk);
        String hmacString = Base64.toBase64String(hmac);
        if (!hmacString.equals(beginCheckPoint.getMetadata().getHmac())) {
            throw new SecureLogBundleValidationException("Begin checkPoint HMAC not valid", beginCheckPoint.getHost());
        }
        return hmac;
    }

    private byte[] concat(byte[]... arrays) {
        byte[] result = null;
        for (byte[] array : arrays) {
            if (result == null) {
                result = array;
            } else {
                byte[] temp = new byte[result.length + array.length];
                System.arraycopy(result, 0, temp, 0, result.length);
                System.arraycopy(array, 0, temp, result.length, array.length);
                result = array;
            }
        }
        return result;
    }
}
