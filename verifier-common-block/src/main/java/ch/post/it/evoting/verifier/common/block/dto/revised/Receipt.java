package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Receipt {
    public final String receipt;
    public final String signature;

    @JsonCreator
    public Receipt(@JsonProperty("receipt") String receipt, @JsonProperty("signature") String signature) {
        this.receipt = receipt;
        this.signature = signature;
    }

    public byte[] getReceipt() {
        return TypeConverter.base64ToByte(receipt);
    }

    public byte[] getSignature() {
        return TypeConverter.base64ToByte(signature);
    }
}
