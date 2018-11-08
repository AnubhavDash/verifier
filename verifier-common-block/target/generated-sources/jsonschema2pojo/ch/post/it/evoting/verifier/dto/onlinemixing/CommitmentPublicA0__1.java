
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "element"
})
public class CommitmentPublicA0__1 {

    @JsonProperty("element")
    private Element__10 element;

    @JsonProperty("element")
    public Element__10 getElement() {
        return element;
    }

    @JsonProperty("element")
    public void setElement(Element__10 element) {
        this.element = element;
    }

}
