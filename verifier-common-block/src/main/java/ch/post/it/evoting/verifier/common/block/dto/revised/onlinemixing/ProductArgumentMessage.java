package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ProductArgumentMessage {

    private Commitment commitmentB;
    private SingleValueArgumentInitMessage singleValueArgumentInitMessage;
    private SingleValueArgumentAnswer singleValueArgumentAnswer;
    private HadamardProductArgumentInitMessage hadamardProductArgumentInitMessage;
    private HadamardProductArgumentAnswer hadamardProductArgumentAnswer;

    public ProductArgumentMessage(@JsonProperty("commitmentPublicB") Commitment commitmentPublicB,
                                  @JsonProperty("iniSVA") SingleValueArgumentInitMessage iniSVA,
                                  @JsonProperty("ansSVA") SingleValueArgumentAnswer ansSVA,
                                  @JsonProperty("iniHPA") HadamardProductArgumentInitMessage iniHPA,
                                  @JsonProperty("ansHPA") HadamardProductArgumentAnswer ansHPA) {
        this.commitmentB = commitmentPublicB;
        this.singleValueArgumentInitMessage = iniSVA;
        this.singleValueArgumentAnswer = ansSVA;
        this.hadamardProductArgumentInitMessage = iniHPA;
        this.hadamardProductArgumentAnswer = ansHPA;
    }
}
