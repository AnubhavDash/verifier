package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.EncryptedBallotsLoader;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.io.ElGamalEncryptedBallotEntryParser;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class OfflineEncryptedBallotsLoader implements EncryptedBallotsLoader {

    private final Path path;
    private ZpGroupParams zGroupParam;

    public OfflineEncryptedBallotsLoader(Path path) { this.path = path; }


    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() throws IOException {
        final ElGamalEncryptedBallots result;

        this.zGroupParam = new OfflineEncryptionParametersLoader(path).getZpGroup().getParams();

        // NOTE class below needed to be change to public
        ElGamalEncryptedBallotEntryParser entryParser = new ElGamalEncryptedBallotEntryParser(zGroupParam);
        //Path fullPath = Paths.get(outputParentPath.toString(), batchName, fileName);
        Path fullPath = PathHelper.getFile(path.toFile(), "encryptedBallots.csv").toPath();
        try (Reader reader = new FileReader(fullPath.toString());
             CSVReader<ElGamalEncryptedBallot> elGamalEncryptedBallotReader =
                     new CSVReaderBuilder<ElGamalEncryptedBallot>(reader).entryParser(entryParser).build()) {

            // TODO[Javi] check csv size limit to load on memory here
            result = new ElGamalEncryptedBallots(elGamalEncryptedBallotReader.readAll());
        }

        return result;
    }

}
