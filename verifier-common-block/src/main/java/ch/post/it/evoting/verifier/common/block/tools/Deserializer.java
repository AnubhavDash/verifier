package ch.post.it.evoting.verifier.common.block.tools;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

public class Deserializer {

    private Deserializer() {
        //private constructor, use static
    }

    public static <T> T fromJson(File inputDirectory, String filename, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(getFile(inputDirectory, filename), targetClazz);
    }

    public static <T> T fromXml(File inputDirectory, String filename, Class<T> targetClazz) throws IOException, JAXBException {
        return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(new FileInputStream(getFile(inputDirectory, filename)));
    }

    private static File getFile(File inputDirectory, String filename) throws FileNotFoundException {
        File[] file = inputDirectory.listFiles((dir, name) -> name.endsWith(filename));
        if (file.length == 0) {
            throw new FileNotFoundException(filename);
        } else if (file.length > 1) {
            throw new InvalidParameterException("more than one file found, filename is not specific enough");
        } else {
            return file[0];
        }
    }
}
