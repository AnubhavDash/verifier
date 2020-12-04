package ch.post.it.evoting.verifier.common.block.dto.revised.algo;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SVPStatement implements Statement {

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger c_a;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger b;

}
