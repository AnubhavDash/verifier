package ch.post.it.evoting.verifier.common.block.tools;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class HmacGenerator {
    private HmacGenerator() {
        //private ctor, use static methods
    }

    public static byte[] Hash(byte[] text, byte[] key) {
        HMac hmac = new HMac(new SHA256Digest());

        hmac.init(new KeyParameter(key));
        byte[] result = new byte[hmac.getMacSize()];
        hmac.update(text, 0, text.length);
        hmac.doFinal(result, 0);

        return result;
    }
}
