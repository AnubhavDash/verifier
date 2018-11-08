
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "element"
})
public class CommitmentPublicA0 {

    @JsonProperty("element")
    private Element__7 element;

    @JsonProperty("element")
    public Element__7 getElement() {
        return element;
    }

    @JsonProperty("element")
    public void setElement(Element__7 element) {
        this.element = element;
    }

}
