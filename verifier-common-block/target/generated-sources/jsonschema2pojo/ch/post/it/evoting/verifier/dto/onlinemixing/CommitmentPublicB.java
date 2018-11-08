
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "element"
})
public class CommitmentPublicB {

    @JsonProperty("element")
    private Element__2 element;

    @JsonProperty("element")
    public Element__2 getElement() {
        return element;
    }

    @JsonProperty("element")
    public void setElement(Element__2 element) {
        this.element = element;
    }

}
