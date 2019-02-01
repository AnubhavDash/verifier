package ch.post.it.evoting.verifier.block.block2.securelog;

import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecureLogBundle {
    private static final Logger LOGGER = Logger.getLogger(SecureLogBundle.class);

    private CheckPointLogEntry beginCheckPoint;
    private CheckPointLogEntry endCheckPoint;
    private List<RegularLogEntry> regularLogEntries = new ArrayList<>();
    private byte[] pem;

    public byte[] getPem() {
        return pem;
    }

    public void setPem(byte[] pem) {
        this.pem = pem;
    }

    public void setBeginCheckPoint(CheckPointLogEntry beginCheckPoint) {
        this.beginCheckPoint = beginCheckPoint;
    }

    public void setEndCheckPoint(CheckPointLogEntry endCheckPoint) {
        this.endCheckPoint = endCheckPoint;
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
            throw new SecureLogBundleValidationException("bundle is not finishing with a checkPoint", beginCheckPoint.getHost(), beginCheckPoint.getSource());
        }
        LOGGER.trace(String.format("Starting validation of Bundle{prev:%s, curr:%s, elementsCount:%s}", this.beginCheckPoint, this.endCheckPoint, this.regularLogEntries.size()));
        byte[] beginHmac = validateStartCheckPoint();
        byte[] lastHmac = validateRegularLogs(beginHmac);
        validateEndCheckPoint(lastHmac);
    }

    public CheckPointLogEntry getBeginCheckPoint() {
        return beginCheckPoint;
    }

    public void validateSignature() throws SecureLogBundleValidationException {
        String bytes = String.format("%s {*LSK::%s,ESK::%s,PHMAC::%s,LS::%s,TL::%s,TS::%s,HMAC::%s*}\n",
                this.getBeginCheckPoint().getRaw().substring(0, this.getBeginCheckPoint().getRaw().length() - 1), //remove the ending \n
                this.getBeginCheckPoint().getMetadata().getLsk(),
                this.getBeginCheckPoint().getMetadata().getEsk(),
                this.getBeginCheckPoint().getMetadata().getPhmac(),
                this.getBeginCheckPoint().getMetadata().getLs(),
                this.getBeginCheckPoint().getMetadata().getTl(),
                this.getBeginCheckPoint().getMetadata().getTs(),
                this.getBeginCheckPoint().getMetadata().getHmac());

        if (this.getPem() == null || !SignatureChecker.verifySignature(bytes.getBytes(StandardCharsets.UTF_8),
                TypeConverter.base64ToByte(this.getBeginCheckPoint().getMetadata().getSg()), this.getPem())) {
            throw new SecureLogBundleValidationException("Begin Checkpoint signature not valid", beginCheckPoint.getHost(), beginCheckPoint.getSource());
        }
    }

    private byte[] concat(String... element) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(element).forEach(sb::append);
        return TypeConverter.stringToByte(sb.toString());
    }

    private String buildSignature(String secret, byte[] text) {
        return null;
    }

    private void validateEndCheckPoint(byte[] lastHmac) throws SecureLogBundleValidationException {
        if (!endCheckPoint.getMetadata().getPhmac().equals(Base64.toBase64String(lastHmac))) {
            throw new SecureLogBundleValidationException("End Checkpoint HMAC not valid", beginCheckPoint.getHost(), beginCheckPoint.getSource());
        }
    }

    private byte[] validateRegularLogs(byte[] beginCheckPointHmac) throws SecureLogBundleValidationException {
        byte[] lsk = Base64.decode(endCheckPoint.getMetadata().getLsk());
        byte[] previousHmac = beginCheckPointHmac.clone();
        for (RegularLogEntry regularLogEntry : regularLogEntries) {
            byte[] hmac = hmac(regularLogEntry, previousHmac, lsk);
            if (!Base64.toBase64String(hmac).equals(regularLogEntry.getMetadata().getHmac())) {
                throw new SecureLogBundleValidationException("Regular log HMAC not valid", beginCheckPoint.getHost(), beginCheckPoint.getSource());
            }
            previousHmac = hmac;
        }
        return previousHmac;
    }

    private byte[] validateStartCheckPoint() throws SecureLogBundleValidationException {
        byte[] lsk = Base64.decode(endCheckPoint.getMetadata().getLsk());
        byte[] hmac = hmac(beginCheckPoint, null, lsk);
        if (!Base64.toBase64String(hmac).equals(beginCheckPoint.getMetadata().getHmac())) {
            throw new SecureLogBundleValidationException("Begin checkPoint HMAC not valid", beginCheckPoint.getHost(), beginCheckPoint.getSource());
        }
        return hmac;
    }

    private byte[] hmac(SecureLogEntry secureLogEntry, byte[] pHmac, byte[] lsk) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(bytes)) {
            if (secureLogEntry instanceof CheckPointLogEntry) {
                stream.write(Base64.decode(secureLogEntry.getMetadata().getPhmac()));
            } else {
                stream.write(pHmac);
            }
            if (StringUtils.isNotEmpty(secureLogEntry.getMetadata().getLsk())) {
                stream.write(Base64.decode(secureLogEntry.getMetadata().getLsk()));
            }
            if (StringUtils.isNotEmpty(secureLogEntry.getMetadata().getEsk())) {
                stream.write(Base64.decode(secureLogEntry.getMetadata().getEsk()));
            }
            if (StringUtils.isNotEmpty(secureLogEntry.getMetadata().getLs())) {
                stream.writeInt(Integer.parseInt(secureLogEntry.getMetadata().getLs()));
            }
            if (StringUtils.isNotEmpty(secureLogEntry.getMetadata().getTl())) {
                stream.writeLong(Long.parseLong(secureLogEntry.getMetadata().getTl()));
            }
            stream.writeLong(Long.parseLong(secureLogEntry.getMetadata().getTs()));
            stream.write(secureLogEntry.getRaw().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize secureLogEntry", e);
        }
        return HmacGenerator.hash(bytes.toByteArray(), lsk);
    }
}
