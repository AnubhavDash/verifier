package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Deserializer {

    private Deserializer() {
        //private constructor, use static
    }

    public static <T> T fromJson(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(getFile(inputDirectory, filenamePattern), targetClazz);
    }

    public static <T> T fromJson(byte[] content, Class<T> targetClazz) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(new String(content), targetClazz);
    }

    public static void toJson(Object content, File file) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.writeValue(file, content);
    }

    public static <T> T fromXml(File inputDirectory, String filenamePattern, Class<T> targetClazz) throws IOException, JAXBException {
        return (T) JAXBContext.newInstance(targetClazz).createUnmarshaller().unmarshal(new FileInputStream(getFile(inputDirectory, filenamePattern)));
    }

    public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, Function<String[], T> mapper) throws IOException {
        return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, ",", mapper).process();
    }

    public static <T> Iterable<T> fromCsv(File inputDirectory, String filenamePattern, String separator, Function<String[], T> mapper) throws IOException {
        return new CsvReader<>(getFile(inputDirectory, filenamePattern).toString(), StandardCharsets.UTF_8, false, separator, mapper).process();
    }

    private static File getFile(File inputDirectory, String filenamePattern) throws FileNotFoundException {
        return PathHelper.getFile(inputDirectory, filenamePattern);
    }

    public static Function<String[], CredentialDataElement> toCredentialDataElement = array -> {
        if (array == null || array.length != 2) {
            throw new IllegalArgumentException("Wrong array input format");
        }
        CredentialDataElement cde = new CredentialDataElement();
        cde.setValue1(array[0]);
        cde.setValue2(array[1]);
        return cde;
    };
}
