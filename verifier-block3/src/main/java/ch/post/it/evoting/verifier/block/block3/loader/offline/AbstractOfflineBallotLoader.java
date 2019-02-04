package ch.post.it.evoting.verifier.block.block3.loader.offline;

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

public class AbstractOfflineBallotLoader {
    protected final Path path;
    private final ZpGroupParams param;

    public AbstractOfflineBallotLoader(Path path, Path rootPath) throws IOException {
        this.path = path;
        this.param = new OfflineEncryptionParametersLoader(rootPath).getZpGroup().getParams();
    }

    protected ElGamalEncryptedBallots get(Path fullPath) throws IOException {
        final ElGamalEncryptedBallots result;

        ElGamalEncryptedBallotEntryParser entryParser = new ElGamalEncryptedBallotEntryParser(this.param);

        try (Reader reader = new FileReader(fullPath.toString());
             CSVReader<ElGamalEncryptedBallot> elGamalEncryptedBallotReader =
                     new CSVReaderBuilder<ElGamalEncryptedBallot>(reader).entryParser(entryParser).build()) {

            result = new ElGamalEncryptedBallots(elGamalEncryptedBallotReader.readAll());
        }

        return result;
    }
}
