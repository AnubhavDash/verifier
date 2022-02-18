/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.core.internal.dto.revised;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.Base64BigIntegerDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.ElectionEventDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.ListDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.PublicKeyDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.UuidDeserializer;
import ch.post.it.evoting.verifier.core.internal.dto.revised.serialization.X509Deserializer;

class JsonParserTest {
	private static ObjectMapper mapper;

	@BeforeAll
	static void init() {
		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);

		SimpleModule typesModule = new SimpleModule();
		typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		typesModule.addDeserializer(UUID.class, new UuidDeserializer());
		typesModule.addDeserializer(List.class, new ListDeserializer());
		typesModule.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
		typesModule.addDeserializer(X509Certificate.class, new X509Deserializer());
		typesModule.addDeserializer(ElectionEvent.class, new ElectionEventDeserializer());
		mapper.registerModule(typesModule);
	}

	@Test
	void deserializeDataconfig() throws IOException {
		InputStream inputStream = getResourceStream("schemas/json/dataConfig_[EE].json");
		ElectionEvent electionEvent = mapper.readValue(inputStream, ElectionEvent.class);

		assertThat(electionEvent.getAlias(), CoreMatchers.equalTo("national_contest_04"));

		assertNotNull(electionEvent.getBallotBoxes());
		assertFalse(electionEvent.getBallotBoxes().isEmpty());

		BallotBox ballotBox = electionEvent.getBallotBoxes().get(0);
		assertNotNull(ballotBox.getCountingCircles());
		assertFalse(ballotBox.getCountingCircles().isEmpty());

		CountingCircle countingCircle = ballotBox.getCountingCircles().get(0);
		assertNotNull(countingCircle.getDomainsOfInfluence());
		assertFalse(countingCircle.getDomainsOfInfluence().isEmpty());
	}

	@Test
	void deserializeMetadata() throws IOException {
		InputStream inputStream = getResourceStream("schemas/json/metadata.json");
		Metadata metadata = mapper.readValue(inputStream, Metadata.class);

		assertNotNull(metadata);
		assertEquals("toto", metadata.getAlgorithm());
		assertEquals("1.0", metadata.getVersion());
		assertNotNull(metadata.getSignedItems());
		assertEquals(2, metadata.getSignedItems().size());
		assertArrayEquals(DatatypeConverter.parseBase64Binary(
				"GhcMhwBb1b1ngv9xvcWvXYHdgchaX5fF0tz5WIPBi2E0aYzZpqmFEylaAJ0XfvmSoqwc3fePMKdUKYXG2JY3tXM1LG70YT6azBFYG038jWaCXXz6NyUkYAz0Oz2vICck53ksyH9PY1zd2QzSwWz8L7bznBhTKgL5/UsuLqcCDvQXLYc82vxOUoIkP4HsreTMKdA5YnaoZjJg/2brDKdqcf2oWvahOI9QDu5+guHZhEOMK7cseQr/1dl3DmgjdaqoXQx5xjd2qemiu+70E6L+g2xk29X0VLiPDqLKF4a8KLB/VyJCkbYYm0VDIogl8mxB91imHo4q5FlC2g1Fjw6RIA=="),
				metadata.getSignature());
	}

	private InputStream getResourceStream(String resource) {
		return this.getClass().getClassLoader().getResourceAsStream(resource);
	}
}
