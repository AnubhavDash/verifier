package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.SecureLogsData;
import ch.post.it.evoting.verifier.dto.SecureLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecureLogsHelper {

    private static final String IT_EVOTING_CC_VALUE = "it_evoting_cc";

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
        int size = logs.size();
        // Filter the entries containing the secure logs of the control components, by filtering the index “it_evoting_cc”
        List<SecureLog> itEvotingCcList = logs.stream()
                                                .filter(log -> log.getResult().getIndex().equalsIgnoreCase(IT_EVOTING_CC_VALUE))
                                                .collect(Collectors.toList());

        int size1 = itEvotingCcList.size();
        // Read the “host” of each entry
        Map<SecureLog, String> logsHostsMap = itEvotingCcList.stream()
                                                                .map(log -> {
                                                                    String host = log.getResult().getHost();
                                                                    return new AbstractMap.SimpleEntry<>(log, host);
                                                                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


        return result;
    }

}
