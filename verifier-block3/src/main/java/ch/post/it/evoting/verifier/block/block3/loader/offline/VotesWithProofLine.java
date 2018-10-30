package ch.post.it.evoting.verifier.block.block3.loader.offline;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotesWithProofLine {
    private String encryptedBallot;
    private String plainText;
    private String proof;
}
