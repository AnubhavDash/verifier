package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class X509Deserializer extends JsonDeserializer<X509Certificate> {
    private final CertificateFactory factory;

    public X509Deserializer() {
        CertificateFactory tmp = null;
        try {
            tmp = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            // TODO: Log exception...
        } finally {
            factory = tmp;
        }

    }

    @Override
    public X509Certificate deserialize(JsonParser jsonParser,
                                       DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
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
