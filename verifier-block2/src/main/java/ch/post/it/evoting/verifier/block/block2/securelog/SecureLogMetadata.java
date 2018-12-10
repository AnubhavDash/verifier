package ch.post.it.evoting.verifier.block.block2.securelog;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecureLogMetadata {
    private String sg;
    private String lsk;
    private String esk;
    private String hmac;
    private String phmac;
    private String ls;
    private String tl;
    private String ts;

}
