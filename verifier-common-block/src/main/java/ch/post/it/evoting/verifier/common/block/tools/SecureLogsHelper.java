package ch.post.it.evoting.verifier.common.block.tools;

import ch.post.it.evoting.verifier.common.block.dto.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.dto.SecureLogsData;
import ch.post.it.evoting.verifier.dto.SecureLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SecureLogsHelper {

    private static final String IT_EVOTING_CC_INDEX_VALUE = "it_evoting_cc";
    private static final String HOSTNAME_LABEL_IN_CSV = "hostname";

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
        List<SecureLog> itEvotingCcList = logs.stream()
                                                .filter(log -> log.getResult().getIndex().equalsIgnoreCase(IT_EVOTING_CC_INDEX_VALUE))
                                                .collect(Collectors.toList());

        // Read the “host” of each entry
        Map<SecureLog, String> logsHostsMap = itEvotingCcList.stream()
                                                                .map(log -> {
                                                                    String host = log.getResult().getHost();
                                                                    return new AbstractMap.SimpleEntry<>(log, host);
                                                                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        //Assign the control components to the entry based on the “host” and the mapping_cc_hosts.csv
        Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", Deserializer.toHostMappingElement);
            // create host/CC mapping
        Map<String, String> hostCcMapping = StreamSupport.stream(iterable.spliterator(), false)
                .filter(hme -> !hme.getHostname().equalsIgnoreCase(HOSTNAME_LABEL_IN_CSV))
                .map(hme -> {
                    return new AbstractMap.SimpleEntry<>(hme.getHostname(), hme.getCc());
                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

          // create logsCCsMap based on logsHostsMap and host/CC mapping
        Map<SecureLog, String> logsCCsMap = logsHostsMap.keySet()
                .stream()
                .map(log -> {
                    String host = logsHostsMap.get(log);
                    String cc = hostCcMapping.get(host);
                    return new AbstractMap.SimpleEntry<>(log, cc);
                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        // create map<source, <log, timeStamp>
        Map<String, Map<SecureLog, Date>> map = new HashMap<>();
        logsCCsMap.keySet()
                .forEach( log -> {
                    String source = log.getResult().getSource();
                    Map<SecureLog, Date> secureLogDateMap = new HashMap();
                    if (map.get(source) == null){
                        map.put(source, secureLogDateMap);
                    }
                    secureLogDateMap  = map.get(source);
                    String raw = log.getResult().getRaw();
                    String[] split = raw.split("\\|");
                    String timeStamp = split[0];
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,S");
                    Date logTimeStamp = null;
                    try {
                        logTimeStamp = format.parse(timeStamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    secureLogDateMap.put(log, logTimeStamp);
                    map.put(source, secureLogDateMap);
                });



        //Output:
        //Data structure with chronologically ordered secure logs, per control component, per host
        return result;
    }

}
