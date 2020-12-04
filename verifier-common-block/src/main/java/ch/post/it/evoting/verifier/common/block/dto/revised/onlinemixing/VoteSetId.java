package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.bouncycastle.util.StringList;

import java.math.BigInteger;

@Getter
public class VoteSetId {

    private final BallotBoxId ballotBoxId;
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger index;

    public VoteSetId(@JsonProperty("ballotBoxId") BallotBoxId ballotBoxId,
                     @JsonProperty("index") BigInteger index) {
        this.ballotBoxId = ballotBoxId;
        this.index = index;
    }
}
