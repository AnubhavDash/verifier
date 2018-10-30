package ch.post.it.evoting.verifier.block.block2.secureLog;

import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public void validateIntegrity() throws SecureLogBundleValidationException {
        if (!this.isComplete() && this.hasRegularLogEntries()) {
            throw new SecureLogBundleValidationException("bundle is not finishing with a checkPoint", beginCheckPoint.getHost());
        }
        LOGGER.trace(String.format("Starting validation of Bundle{prev:%s, curr:%s, elementsCount:%s}", this.beginCheckPoint, this.endCheckPoint, this.regularLogEntries.size()));
        byte[] beginHmac = validateStartCheckPoint();
        byte[] lastHmac = validateRegularLogs(beginHmac);
        validateEndCheckPoint(lastHmac);
    }

    public void validateSignature() throws SecureLogBundleValidationException {
        String sg = beginCheckPoint.getMetadata().getSg();
        byte[] text = concat(
                beginCheckPoint.getMetadata().getPhmac(),
                beginCheckPoint.getMetadata().getLsk(),
                beginCheckPoint.getMetadata().getEsk(),
                beginCheckPoint.getMetadata().getHmac(),
                beginCheckPoint.getRaw());

        //TODO build the signature and check it
        String signature = /*buildSignature(secret, text);*/ sg;
        if (!sg.equals(signature)) {
            throw new SecureLogBundleValidationException("Begin Checkpoint signature not valid", beginCheckPoint.getHost());
        }
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
                    Base64.toBase64String(previousHmac),
                    regularLogEntry.getRaw());
            byte[] hmac = HmacGenerator.hash(text, lsk);
            if (!Base64.toBase64String(hmac).equals(regularLogEntry.getMetadata().getHmac())) {
                throw new SecureLogBundleValidationException("Regular log HMAC not valid", beginCheckPoint.getHost());
            }
            previousHmac = hmac;
        }
        return previousHmac;
    }

    private byte[] validateStartCheckPoint() throws SecureLogBundleValidationException {
        byte[] lsk = Base64.decode(endCheckPoint.getMetadata().getLsk());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            stream.write(Base64.decode(beginCheckPoint.getMetadata().getPhmac()));
            stream.write(Base64.decode(beginCheckPoint.getMetadata().getLsk()));
            stream.write(Base64.decode(beginCheckPoint.getMetadata().getEsk()));
            stream.writeLong(Long.parseLong(beginCheckPoint.getMetadata().getTs()));
            stream.write(beginCheckPoint.getRaw().getBytes(StandardCharsets.UTF_8));
            bytes.close();
        } catch (IOException e) {
            throw new RuntimeException("error during generating the text for the HMAC generation", e);
        }
        byte[] hmac = HmacGenerator.hash(bytes.toByteArray(), lsk);
        if (!Base64.toBase64String(hmac).equals(beginCheckPoint.getMetadata().getHmac())) {
            throw new SecureLogBundleValidationException("Begin checkPoint HMAC not valid", beginCheckPoint.getHost());
        }
        return hmac;
    }

    private byte[] concat(String... arrays) {
        StringBuilder result = new StringBuilder();
        for (String array : arrays) {
            result.append(array);
        }
        return result.toString().getBytes(StandardCharsets.UTF_8);
    }
}
