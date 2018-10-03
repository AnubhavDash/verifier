package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.SecureLogsData;
import ch.post.it.evoting.verifier.dto.SecureLogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SecureLogsHelper {

    private SecureLogsHelper() {
    }

    public static SecureLogsData ParseSecureLogs(Path secureLogs, File mapping) throws IOException {
        SecureLogsData result = new SecureLogsData();
       //  Read the JSON file containing the SecureLogs
        SecureLogs sl = Deserializer.fromJson(secureLogs.getParent().toFile(), secureLogs.getFileName().toString(), SecureLogs.class);
        // Filter the entries containing the secure logs of the control components, by filtering the index “it_evoting_cc”
        // Read the “host” of each entry
        return result;
    }

}
