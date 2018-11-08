/**
 * @author jruiz
 * @date 23/06/15 18:13
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.EncryptionParametersZpSubGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ZpGroupReader {

	public static ZpGroup build(final Path encryptionParameters) throws IOException {

		final Properties config = new Properties();
		final File propertiesFile = encryptionParameters.toFile();
		/*try (final FileInputStream inputStream = new FileInputStream(propertiesFile)) {
			config.load(inputStream);
		}*/
		EncryptionParametersZpSubGroup json = Deserializer.fromJson(encryptionParameters.getParent().toFile(), encryptionParameters.getFileName().toString(), EncryptionParametersZpSubGroup.class);

		ZpGroupParams zpGroupParams = new ZpGroupParams(TypeConverter.base64ToBigInteger(json.getZpSubgroup().getP()),
				TypeConverter.base64ToBigInteger(json.getZpSubgroup().getQ()));
		ZpElement zpElement = new ZpElement(TypeConverter.base64ToBigInteger(json.getZpSubgroup().getG()), zpGroupParams);

		return new ZpGroup(zpGroupParams, zpElement);
	}

	public static void serializeToFile(final ZpGroup zpGroup, final Path pathOutputFile) throws Exception {

		final List<String> linesToBeWritten = new ArrayList<>();

		// write the group information
		linesToBeWritten.add("p=" + zpGroup.getP().toString());
		linesToBeWritten.add("q=" + zpGroup.getOrder().toString());
		linesToBeWritten.add("g=" + zpGroup.getGenerator().toString());

		FileUtils.writeLines(pathOutputFile.toFile(), linesToBeWritten);
	}

	public static ZpGroup createZpGroupFromParameterStrings(final String pAsString, final String qAsString,
			final String gAsString) {

		final BigInteger pAsBigInteger = new BigInteger(pAsString);
		final BigInteger qAsBigInteger = new BigInteger(qAsString);

		final ZpGroupParams zpGroupParams = new ZpGroupParams(pAsBigInteger, qAsBigInteger);

		//final ZpGroup reconstructedZpGroup = new ZpGroup(pAsBigInteger, qAsBigInteger, new ZpElement(gAsString, zpGroupParams));
		return new ZpGroup(pAsBigInteger, qAsBigInteger, new ZpElement(gAsString, zpGroupParams));
	}
}
