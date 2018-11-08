
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "class",
    "randomnessValue"
})
public class RandomnessTau {

    @JsonProperty("class")
    private String _class;
    @JsonProperty("randomnessValue")
    private RandomnessValue randomnessValue;

    @JsonProperty("class")
    public String getClass_() {
        return _class;
    }

    @JsonProperty("class")
    public void setClass_(String _class) {
        this._class = _class;
    }

    @JsonProperty("randomnessValue")
    public RandomnessValue getRandomnessValue() {
        return randomnessValue;
    }

    @JsonProperty("randomnessValue")
    public void setRandomnessValue(RandomnessValue randomnessValue) {
        this.randomnessValue = randomnessValue;
    }

}
