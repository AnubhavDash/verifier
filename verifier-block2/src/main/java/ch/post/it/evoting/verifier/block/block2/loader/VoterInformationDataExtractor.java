/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.loader;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
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
        List<File> voterInformationFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2VerificationSuite.PATH_ELECTION_SETUP).resolve(Block2VerificationSuite.PATH_VOTING_CARD_SETS).toFile(),
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



