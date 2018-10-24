
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "preview",
    "offset",
    "result"
})
public class SecureLog {

    @JsonProperty("preview")
    private Boolean preview;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("result")
    private Result__1 result;

    @JsonProperty("preview")
    public Boolean getPreview() {
        return preview;
    }

    @JsonProperty("preview")
    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    @JsonProperty("offset")
    public Integer getOffset() {
        return offset;
    }

    @JsonProperty("offset")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @JsonProperty("result")
    public Result__1 getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(Result__1 result) {
        this.result = result;
    }

}
