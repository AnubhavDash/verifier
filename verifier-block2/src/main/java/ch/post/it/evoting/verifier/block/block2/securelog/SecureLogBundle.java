/*
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

import ch.post.it.evoting.verifier.common.block.tools.HmacGenerator;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

public class SecureLogBundle {
	private static final Logger LOGGER = Logger.getLogger(SecureLogBundle.class);

	private CheckPointLogEntry beginCheckPoint;
	private CheckPointLogEntry endCheckPoint;
	private final List<RegularLogEntry> regularLogEntries = new ArrayList<>();
	private SecureLogBundleCertificates certificates;

	public void setCertificates(SecureLogBundleCertificates certificates) {
		this.certificates = certificates;
	}

	public CheckPointLogEntry getBeginCheckPoint() {
		return this.beginCheckPoint;
	}

	public void setBeginCheckPoint(CheckPointLogEntry beginCheckPoint) {
		this.beginCheckPoint = beginCheckPoint;
	}

	public CheckPointLogEntry getEndCheckPoint() {
		return this.endCheckPoint;
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

	public boolean validateIntegrity() {
		try {
			if (!this.isComplete() && this.hasRegularLogEntries()) {
				throw new SecureLogBundleValidationException("bundle is not finishing with a checkPoint", beginCheckPoint.getHost(),
						beginCheckPoint.getSource());
			}
			LOGGER.trace(String.format("Starting validation of Bundle{prev:%s, curr:%s, elementsCount:%s}", this.beginCheckPoint, this.endCheckPoint,
					this.regularLogEntries.size()));
			byte[] beginHmac = validateStartCheckPoint();
			byte[] lastHmac = validateRegularLogs(beginHmac);
			validateEndCheckPoint(lastHmac);
			return true;
		} catch (SecureLogBundleValidationException e) {
			LOGGER.error("Validation failed on host {" + e.getHost() + "}, source {" + e.getSource() + "} : " + e.getMessage());
			return false;
		}
	}

	public boolean validateSignature() {
		if (StringUtils.isEmpty(beginCheckPoint.getMetadata().getPhmac())) {
			//This is the first Bundle, also check the begin checkpoint signature
			String source = String.format("%s {*ESK::%s,LS::%s,TL::%s,TS::%s,HMAC::%s*}\n",
					beginCheckPoint.getRaw().substring(0, beginCheckPoint.getRaw().length() - 1), //remove the ending \n
					beginCheckPoint.getMetadata().getEsk(),
					beginCheckPoint.getMetadata().getLs(),
					beginCheckPoint.getMetadata().getTl(),
					beginCheckPoint.getMetadata().getTs(),
					beginCheckPoint.getMetadata().getHmac());

			if (!validateSignature(source, beginCheckPoint.getMetadata().getSg())) {
				LOGGER.error(String.format("Begin Checkpoint signature not valid. Host: %s Source: %s", beginCheckPoint.getHost(),
						beginCheckPoint.getSource()));
				return false;
			}
		}

		String source = String.format("%s {*LSK::%s,ESK::%s,PHMAC::%s,LS::%s,TL::%s,TS::%s,HMAC::%s*}\n",
				endCheckPoint.getRaw().substring(0, endCheckPoint.getRaw().length() - 1), //remove the ending \n
				endCheckPoint.getMetadata().getLsk(),
				endCheckPoint.getMetadata().getEsk(),
				endCheckPoint.getMetadata().getPhmac(),
				endCheckPoint.getMetadata().getLs(),
				endCheckPoint.getMetadata().getTl(),
				endCheckPoint.getMetadata().getTs(),
				endCheckPoint.getMetadata().getHmac());

		if (!validateSignature(source, endCheckPoint.getMetadata().getSg())) {
			LOGGER.error(
					String.format("End Checkpoint signature not valid. Host: %s Source: %s", endCheckPoint.getHost(), endCheckPoint.getSource()));
			return false;
		}
		return true;
	}

	private boolean validateSignature(String source, String signature) {
		if (certificates == null) {
			LOGGER.error("No certificates for checking the signature");
			return false;
		} else {
			return SignatureChecker.verifySignature(source.getBytes(StandardCharsets.UTF_8),
					TypeConverter.base64ToByte(signature),
					certificates.getCertificate(),
					certificates.getIntermediate() != null ? new byte[][] { certificates.getIntermediate() } : null,
					certificates.getRoot());
		}
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
				if (StringUtils.isNotEmpty(secureLogEntry.getMetadata().getPhmac())) {
					stream.write(Base64.decode(secureLogEntry.getMetadata().getPhmac()));
				}
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
