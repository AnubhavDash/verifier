package ch.post.it.evoting.verifier.block.block2.loader;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class VoterInformationDataExtractor {
    private VoterInformationDataExtractor() {
        //private ctor, use static
    }

    public static VoterInformationStruct getInfo(File inputDirectory) throws IOException {
        List<File> voterInformationFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_ELECTION_SETUP).resolve(Block2TestSuite.PATH_VOTING_CARD_SETS).toFile(),
                "voterInformation.*\\.csv",
                true);


        VoterInformationStruct voterInformation = Flux.fromStream(voterInformationFiles.stream())
                .flatMap(f -> {
                    try {
                        return Flux.fromStream(Files.lines(f.toPath())).map(s -> s.split(",")[4]);
                    } catch (IOException e) {
                        throw new RuntimeException("An error occurs while parsing the voterInformation.csv files", e.getCause());
                    }
                })
                .reduce(new VoterInformationStruct(), (struct, eeid) -> {
                    struct.increment();
                    struct.setAndCheckUniqueEeid(eeid);
                    return struct;
                })
                .block();

        return voterInformation;
    }


}



