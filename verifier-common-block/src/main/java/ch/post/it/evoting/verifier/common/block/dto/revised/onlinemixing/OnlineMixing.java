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

    private VoteEncryptionKey voteEncryptionKey;
    private VoteSetId voteSetId;
    private List<MixedVote> votes;
    private String electoralAuthorityId;
    private EncryptionGroup encryptionGroup;
    private List<String> decryptionProofs;
    private List<MixedVote> shuffledVotes;
    private String shuffleProof;

    private List<BigInteger> commitmentParameters;
    private String timestamp;
    private Signature signature;
    private List<MixedVote> previousVotes;
    private VoteEncryptionKey previousVoteEncryptionKey;


    public OnlineMixing(@JsonProperty("voteEncryptionKey") VoteEncryptionKey voteEncryptionKey,
                        @JsonProperty("voteSetId") VoteSetId voteSetId,
                        @JsonProperty("votes") MixedVote[] votes,
                        @JsonProperty("electoralAuthorityId") String electoralAuthorityId,
                        @JsonProperty("encryptionParameters") EncryptionGroup encryptionParameters,
                        @JsonProperty("decryptionProofs") List<String> decryptionProofs,
                        @JsonProperty("shuffledVotes") MixedVote[] shuffledVotes,
                        @JsonProperty("shuffleProof") String shuffleProof,
                        @JsonProperty("commitmentParameters") List<BigInteger> commitmentParameters,
                        @JsonProperty("timestamp") String timestamp,
                        @JsonProperty("signature") Signature signature,
                        @JsonProperty("previousVotes") MixedVote[] previousVotes,
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
