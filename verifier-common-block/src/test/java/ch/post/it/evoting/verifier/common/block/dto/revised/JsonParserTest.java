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
package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class JsonParserTest {
    private static ObjectMapper mapper;

    @BeforeClass
    public static void init() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);

        SimpleModule typesModule = new SimpleModule();
        typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
        typesModule.addDeserializer(UUID.class, new UuidDeserializer());
        typesModule.addDeserializer(List.class, new ListDeserializer());
        typesModule.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
        typesModule.addDeserializer(X509Certificate.class, new X509Deserializer());
        typesModule.addDeserializer(AuthenticationToken.class, new AuthenticationTokenDeserializer());
        typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        typesModule.addDeserializer(PreImageProof.class, new PreImageProofDeserializer());
        typesModule.addDeserializer(PlaintextEqualityProof.class, new PlaintextEqualityProofDeserializer());
        typesModule.addDeserializer(ElectionEvent.class, new ElectionEventDeserializer());
        mapper.registerModule(typesModule);
    }

    @Test
    public void deserializeBallot() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/downloadedBallot.json");
        Ballot ballot = mapper.readValue(inputStream, Ballot.class);

        assertThat(ballot.vote.votingCardId,
                   CoreMatchers.equalTo(UUID.fromString("da427fb5-4017-48e6-9cb0-67d5ae65e4b7")));

        assertArrayEquals(ballot.vote.plainTextEqualityProof.getH(), DatatypeConverter.parseHexBinary(
                "0096df8c993e9224883a95ac2bc031baa6f360928d0a6544452ca79b17a62248c5"));
    }

    @Test
    public void deserializePublicKey() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/publicKey.json");
        PublicKey publicKey = mapper.readValue(inputStream, PublicKey.class);

        // Works, thanks to BigInteger's handling of a cache for small values, similar to string.
        // Two instances of BigInteger.valueOf(2) will always be object equal.
        assertThat(publicKey.group.g, CoreMatchers.equalTo(BigInteger.valueOf(2)));
    }

    @Test
    public void deserializeEncryptionParameters() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/encryptionParameters.json");
        EncryptionGroup encryptionGroup = mapper.readValue(inputStream, EncryptionGroup.class);

        assertThat(encryptionGroup.g, CoreMatchers.equalTo(BigInteger.valueOf(2)));
    }

    @Test
    public void deserializeEncryptionParametersZpSubgroup() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/encryptionParametersZpSubGroup.json");
        JsonNode root = mapper.readTree(inputStream);
        JsonNode zpSubgroup = root.path("zpSubgroup");
        EncryptionGroup encryptionGroup = mapper.readValue(zpSubgroup.traverse(), EncryptionGroup.class);

        assertThat(encryptionGroup.g, CoreMatchers.equalTo(BigInteger.valueOf(2)));
    }

    @Test
    public void deserializeDataconfig() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/dataConfig_[EE].json");
        ElectionEvent electionEvent = mapper.readValue(inputStream, ElectionEvent.class);

        assertThat(electionEvent.alias, CoreMatchers.equalTo("national_contest_04"));

        assertNotNull(electionEvent.ballotBoxes);
        assertFalse(electionEvent.ballotBoxes.isEmpty());
        BallotBox ballotBox = electionEvent.ballotBoxes.get(0);
        assertNotNull(ballotBox.countingCircles);
        assertFalse(ballotBox.countingCircles.isEmpty());
        CountingCircle countingCircle = ballotBox.countingCircles.get(0);
        assertNotNull(countingCircle.domainsOfInfluence);
        assertFalse(countingCircle.domainsOfInfluence.isEmpty());
    }

    @Test
    public void deserializeMetadata() throws IOException {
        InputStream inputStream = getResourceStream("schemas/json/metadata.json");
        Metadata metadata = mapper.readValue(inputStream, Metadata.class);

        assertNotNull(metadata);
        assertEquals("toto", metadata.algorithm);
        assertEquals("1.0", metadata.version);
        assertNotNull(metadata.signedItems);
        assertEquals(2, metadata.signedItems.size());
        assertArrayEquals(DatatypeConverter.parseBase64Binary(
                "GhcMhwBb1b1ngv9xvcWvXYHdgchaX5fF0tz5WIPBi2E0aYzZpqmFEylaAJ0XfvmSoqwc3fePMKdUKYXG2JY3tXM1LG70YT6azBFYG038jWaCXXz6NyUkYAz0Oz2vICck53ksyH9PY1zd2QzSwWz8L7bznBhTKgL5/UsuLqcCDvQXLYc82vxOUoIkP4HsreTMKdA5YnaoZjJg/2brDKdqcf2oWvahOI9QDu5+guHZhEOMK7cseQr/1dl3DmgjdaqoXQx5xjd2qemiu+70E6L+g2xk29X0VLiPDqLKF4a8KLB/VyJCkbYYm0VDIogl8mxB91imHo4q5FlC2g1Fjw6RIA=="),
                          metadata.getSignature());

        StringBuilder sb = new StringBuilder();
        metadata.signedItems.stream().forEach(s -> sb.append(s.value));
    }


    private InputStream getResourceStream(String resource) {
        return this.getClass().getClassLoader().getResourceAsStream(
                resource);
    }
}
