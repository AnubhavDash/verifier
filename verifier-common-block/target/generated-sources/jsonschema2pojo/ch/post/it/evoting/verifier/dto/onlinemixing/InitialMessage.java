
package ch.post.it.evoting.verifier.dto.onlinemixing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "element"
})
public class InitialMessage {

    @JsonProperty("element")
    private Element element;

    @JsonProperty("element")
    public Element getElement() {
        return element;
    }

    @JsonProperty("element")
    public void setElement(Element element) {
        this.element = element;
    }

}
