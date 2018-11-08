
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "element"
})
public class CommitmentPublicLowDelta {

    @JsonProperty("element")
    private Element__4 element;

    @JsonProperty("element")
    public Element__4 getElement() {
        return element;
    }

    @JsonProperty("element")
    public void setElement(Element__4 element) {
        this.element = element;
    }

}
