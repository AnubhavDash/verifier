
package ch.post.it.evoting.verifier.dto.onlinemixing;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "voteEncryptionKey",
    "voteSetId",
    "votes",
    "electoralAuthorityId",
    "encryptionParameters",
    "decryptionProofs",
    "shuffledVotes",
    "shuffleProof",
    "commitmentParameters",
    "timestamp",
    "signature",
    "previousVotes",
    "previousVoteEncryptionKey"
})
public class OnlineMixing {

    @JsonProperty("voteEncryptionKey")
    private VoteEncryptionKey voteEncryptionKey;
    @JsonProperty("voteSetId")
    private VoteSetId voteSetId;
    @JsonProperty("votes")
    private List<Vote> votes = new ArrayList<Vote>();
    @JsonProperty("electoralAuthorityId")
    private String electoralAuthorityId;
    @JsonProperty("encryptionParameters")
    private EncryptionParameters encryptionParameters;
    @JsonProperty("decryptionProofs")
    private List<String> decryptionProofs = new ArrayList<String>();
    @JsonProperty("shuffledVotes")
    private List<ShuffledVote> shuffledVotes = new ArrayList<ShuffledVote>();
    @JsonProperty("shuffleProof")
    private String shuffleProof;
    @JsonProperty("commitmentParameters")
    private List<String> commitmentParameters = new ArrayList<String>();
    @JsonProperty("timestamp")
    private Double timestamp;
    @JsonProperty("signature")
    private Signature signature;
    @JsonProperty("previousVotes")
    private List<PreviousVote> previousVotes = new ArrayList<PreviousVote>();
    @JsonProperty("previousVoteEncryptionKey")
    private PreviousVoteEncryptionKey previousVoteEncryptionKey;

    @JsonProperty("voteEncryptionKey")
    public VoteEncryptionKey getVoteEncryptionKey() {
        return voteEncryptionKey;
    }

    @JsonProperty("voteEncryptionKey")
    public void setVoteEncryptionKey(VoteEncryptionKey voteEncryptionKey) {
        this.voteEncryptionKey = voteEncryptionKey;
    }

    @JsonProperty("voteSetId")
    public VoteSetId getVoteSetId() {
        return voteSetId;
    }

    @JsonProperty("voteSetId")
    public void setVoteSetId(VoteSetId voteSetId) {
        this.voteSetId = voteSetId;
    }

    @JsonProperty("votes")
    public List<Vote> getVotes() {
        return votes;
    }

    @JsonProperty("votes")
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @JsonProperty("electoralAuthorityId")
    public String getElectoralAuthorityId() {
        return electoralAuthorityId;
    }

    @JsonProperty("electoralAuthorityId")
    public void setElectoralAuthorityId(String electoralAuthorityId) {
        this.electoralAuthorityId = electoralAuthorityId;
    }

    @JsonProperty("encryptionParameters")
    public EncryptionParameters getEncryptionParameters() {
        return encryptionParameters;
    }

    @JsonProperty("encryptionParameters")
    public void setEncryptionParameters(EncryptionParameters encryptionParameters) {
        this.encryptionParameters = encryptionParameters;
    }

    @JsonProperty("decryptionProofs")
    public List<String> getDecryptionProofs() {
        return decryptionProofs;
    }

    @JsonProperty("decryptionProofs")
    public void setDecryptionProofs(List<String> decryptionProofs) {
        this.decryptionProofs = decryptionProofs;
    }

    @JsonProperty("shuffledVotes")
    public List<ShuffledVote> getShuffledVotes() {
        return shuffledVotes;
    }

    @JsonProperty("shuffledVotes")
    public void setShuffledVotes(List<ShuffledVote> shuffledVotes) {
        this.shuffledVotes = shuffledVotes;
    }

    @JsonProperty("shuffleProof")
    public String getShuffleProof() {
        return shuffleProof;
    }

    @JsonProperty("shuffleProof")
    public void setShuffleProof(String shuffleProof) {
        this.shuffleProof = shuffleProof;
    }

    @JsonProperty("commitmentParameters")
    public List<String> getCommitmentParameters() {
        return commitmentParameters;
    }

    @JsonProperty("commitmentParameters")
    public void setCommitmentParameters(List<String> commitmentParameters) {
        this.commitmentParameters = commitmentParameters;
    }

    @JsonProperty("timestamp")
    public Double getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("signature")
    public Signature getSignature() {
        return signature;
    }

    @JsonProperty("signature")
    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    @JsonProperty("previousVotes")
    public List<PreviousVote> getPreviousVotes() {
        return previousVotes;
    }

    @JsonProperty("previousVotes")
    public void setPreviousVotes(List<PreviousVote> previousVotes) {
        this.previousVotes = previousVotes;
    }

    @JsonProperty("previousVoteEncryptionKey")
    public PreviousVoteEncryptionKey getPreviousVoteEncryptionKey() {
        return previousVoteEncryptionKey;
    }

    @JsonProperty("previousVoteEncryptionKey")
    public void setPreviousVoteEncryptionKey(PreviousVoteEncryptionKey previousVoteEncryptionKey) {
        this.previousVoteEncryptionKey = previousVoteEncryptionKey;
    }

}
