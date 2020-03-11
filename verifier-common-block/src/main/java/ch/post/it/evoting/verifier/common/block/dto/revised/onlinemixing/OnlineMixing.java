package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class OnlineMixing {

    private VoteEncryptionKey voteEncryptionKey;
    private VoteSetId voteSetId;
    private List<Ciphertext> votes;
    private String electoralAuthorityId;
    private EncryptionGroup encryptionGroup;
    private List<String> decryptionProofs;
    private List<Ciphertext> shuffledVotes;
    private String shuffleProof;

    private List<BigInteger> commitmentParameters;
    private String timestamp;
    private Signature signature;
    private List<Ciphertext> previousVotes;
    private VoteEncryptionKey previousVoteEncryptionKey;


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
