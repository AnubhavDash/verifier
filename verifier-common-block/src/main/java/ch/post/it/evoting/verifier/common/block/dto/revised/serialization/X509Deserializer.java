/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class X509Deserializer extends JsonDeserializer<X509Certificate> {
	private final CertificateFactory factory;

	public X509Deserializer() {
		CertificateFactory tmp = null;
		try {
			tmp = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
		} finally {
			factory = tmp;
		}

	}

	@Override
	public X509Certificate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		if (factory != null) {
			String value = jsonParser.getValueAsString();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
			try {
				return (X509Certificate) factory.generateCertificate(inputStream);
			} catch (CertificateException e) {
				throw new InvalidFormatException(jsonParser, "The certificate couldn't be parsed",
						value, X509Certificate.class);
			}
		} else {
			throw new IllegalStateException("Can't parse certificates, certificate factory failed to load");
		}
	}
}
