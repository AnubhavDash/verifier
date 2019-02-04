package ch.post.it.evoting.verifier.block.block2.securelog;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecureLogBundleCertificates {
    private byte[] certificate;
    private byte[] intermediate;
    private byte[] root;
}
