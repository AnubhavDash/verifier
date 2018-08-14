package ch.post.it.evoting.verifier.common.block.tools;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static <T> T fromJson(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(getFile(inputDirectory, filenamePattern), targetClazz);
    }

    public static <T> T fromXml(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException, JAXBException {
        return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(new FileInputStream(getFile(inputDirectory, filenamePattern)));
    }

    private static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        File[] file = inputDirectory.listFiles((dir, name) -> name.matches(filenamePattern));
        if (file.length == 0) {
            throw new FileNotFoundException(filenamePattern);
        } else if (file.length > 1) {
            throw new InvalidParameterException("more than one file found, filename is not specific enough");
        } else {
            return file[0];
        }
    }
}
