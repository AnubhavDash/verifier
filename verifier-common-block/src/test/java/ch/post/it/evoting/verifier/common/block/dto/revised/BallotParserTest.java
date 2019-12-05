package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.dto.revised.serialization.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class BallotParserTest {
    private static ObjectMapper mapper;

    @BeforeClass
    public static void init() throws Exception {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule typesModule = new SimpleModule();
        typesModule.addDeserializer(UUID.class, new UuidFromStringDeserializer());
        typesModule.addDeserializer(List.class, new ListDeserializer());
        typesModule.addDeserializer(PublicKey.class, new PublicKeyDeserializer());
        typesModule.addDeserializer(X509Certificate.class, new X509Deserializer());
        typesModule.addDeserializer(AuthenticationToken.class, new AuthenticationTokenDeserializer());
        typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        typesModule.addDeserializer(PreImageProof.class, new PreImageProofDeserializer());
        typesModule.addDeserializer(PlaintextEqualityProof.class, new PlaintextEqualityProofDeserializer());
        mapper.registerModule(typesModule);
    }

    @Test
    public void deserializeBallot() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                "schemas/json/downloadedBallot.json");
        Ballot ballot = mapper.readValue(inputStream, Ballot.class);

        assertThat(ballot.vote.votingCardId,
                   CoreMatchers.equalTo(UUID.fromString("da427fb5-4017-48e6-9cb0-67d5ae65e4b7")));

        System.out.println(DatatypeConverter.printHexBinary(ballot.vote.plainTextEqualityProof.getH()));

        assertArrayEquals(ballot.vote.plainTextEqualityProof.getH(), DatatypeConverter.parseHexBinary(
                "0096df8c993e9224883a95ac2bc031baa6f360928d0a6544452ca79b17a62248c5"));
    }
}
