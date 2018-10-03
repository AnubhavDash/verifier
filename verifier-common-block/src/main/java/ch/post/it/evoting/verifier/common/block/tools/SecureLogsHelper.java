package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.SecureLogsData;
import ch.post.it.evoting.verifier.dto.SecureLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SecureLogsHelper {

    private SecureLogsHelper() {
    }

    public static SecureLogsData ParseSecureLogs(Path secureLogs, File mapping) throws IOException {
        SecureLogsData result = new SecureLogsData();

       //  Read the JSON file containing the SecureLogs
        List<SecureLog> logs = new ArrayList<>();
        Stream<String> stream = Files.lines(secureLogs);
        logs.add(Deserializer.fromJson(secureLogs.getParent().toFile(), secureLogs.getFileName().toString(), SecureLog.class));
        stream.forEach( line -> {
            try {
                logs.add(Deserializer.fromJson(line.getBytes(), SecureLog.class));
            } catch (IOException e) {
                // TODO Handle exception
                e.printStackTrace();
            }
        });
        // Filter the entries containing the secure logs of the control components, by filtering the index “it_evoting_cc”


        // Read the “host” of each entry

        return result;
    }

}
