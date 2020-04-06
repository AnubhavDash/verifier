package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class OnlineMixing {

    private final VoteEncryptionKey voteEncryptionKey;
    private final VoteSetId voteSetId;
    private final List<Ciphertext> votes;
    private final String electoralAuthorityId;
    private final EncryptionGroup encryptionGroup;
    private final List<String> decryptionProofs;
    private final List<Ciphertext> shuffledVotes;
    private final String shuffleProof;
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private final List<BigInteger> commitmentParameters;
    private final String timestamp;
    private final Signature signature;
    private final List<Ciphertext> previousVotes;
    private final VoteEncryptionKey previousVoteEncryptionKey;


    public OnlineMixing(@JsonProperty("voteEncryptionKey") VoteEncryptionKey voteEncryptionKey,
                        @JsonProperty("voteSetId") VoteSetId voteSetId,
                        @JsonProperty("votes") Ciphertext[] votes,
                        @JsonProperty("electoralAuthorityId") String electoralAuthorityId,
                        @JsonProperty("encryptionParameters") EncryptionGroup encryptionParameters,
                        @JsonProperty("decryptionProofs") List<String> decryptionProofs,
                        @JsonProperty("shuffledVotes") Ciphertext[] shuffledVotes,
                        @JsonProperty("shuffleProof") String shuffleProof,
                        @JsonProperty("commitmentParameters") List<BigInteger> commitmentParameters,
                        @JsonProperty("timestamp") String timestamp,
                        @JsonProperty("signature") Signature signature,
                        @JsonProperty("previousVotes") Ciphertext[] previousVotes,
                        @JsonProperty("previousVoteEncryptionKey") VoteEncryptionKey previousVoteEncryptionKey) {
        this.voteEncryptionKey = voteEncryptionKey;
        this.voteSetId = voteSetId;
        this.votes = ImmutableList.copyOf(votes);
        this.electoralAuthorityId = electoralAuthorityId;
        this.encryptionGroup = encryptionParameters;
        this.decryptionProofs = decryptionProofs;
        this.shuffledVotes = ImmutableList.copyOf(shuffledVotes);
        this.shuffleProof = shuffleProof;
        this.commitmentParameters = commitmentParameters;
        this.timestamp = timestamp;
        this.signature = signature;
        this.previousVotes = ImmutableList.copyOf(previousVotes);
        this.previousVoteEncryptionKey = previousVoteEncryptionKey;
    }
}
