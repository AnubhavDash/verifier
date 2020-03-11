package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Ciphertext {

    private String gamma;
    private String phis;

    public Ciphertext(@JsonProperty("gamma") String gamma,
                      @JsonProperty("phis") String phis) {
        this.gamma = gamma;
        this.phis = phis;
    }
}
