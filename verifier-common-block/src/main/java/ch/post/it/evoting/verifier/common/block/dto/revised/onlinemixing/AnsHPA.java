package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AnsHPA {

    private Initial initial;
    private Answer answer;

    public AnsHPA(@JsonProperty("initial") Initial initial,
                  @JsonProperty("answer") Answer answer) {
        this.initial = initial;
        this.answer = answer;
    }
}
