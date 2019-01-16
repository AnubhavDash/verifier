package ch.post.it.evoting.verifier.securelog;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class Reverser {
    public static void main(String... args) throws Exception {
        if (args == null || args.length != 2) {
            System.out.println("Usage : java -jar verifier-securelogreverser-cli-<VERSION>.jar <inputFile> <outputFile>");
        } else {
            String line;
            try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(args[0]), StandardCharsets.UTF_8)) {
                try (FileWriter fw = new FileWriter(args[1])) {
                    while ((line = reader.readLine()) != null) {
                        fw.write(line + System.lineSeparator());
                    }
                }
            }
        }
    }
}
