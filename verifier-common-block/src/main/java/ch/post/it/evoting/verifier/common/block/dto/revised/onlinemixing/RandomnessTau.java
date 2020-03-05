package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class RandomnessTau {

    private ExponentValue randomnessValue;

    public RandomnessTau(@JsonProperty("randomnessValue") ExponentValue randomnessValue) {
        this.randomnessValue = randomnessValue;
    }
}
