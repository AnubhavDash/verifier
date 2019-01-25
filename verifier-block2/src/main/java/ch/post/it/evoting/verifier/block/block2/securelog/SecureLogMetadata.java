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

    @Override
    public String toString() {
        return "SecureLogMetadata{" +
                "sg='" + sg + '\'' +
                ", lsk='" + lsk + '\'' +
                ", esk='" + esk + '\'' +
                ", hmac='" + hmac + '\'' +
                ", phmac='" + phmac + '\'' +
                ", ls='" + ls + '\'' +
                ", tl='" + tl + '\'' +
                ", ts='" + ts + '\'' +
                '}';
    }
}
