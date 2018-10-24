
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "preview",
    "offset",
    "lastrow",
    "result"
})
public class SecureLogOrigin {

    @JsonProperty("preview")
    private Boolean preview;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("lastrow")
    private Boolean lastrow;
    @JsonProperty("result")
    private Result result;

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

    @JsonProperty("lastrow")
    public Boolean getLastrow() {
        return lastrow;
    }

    @JsonProperty("lastrow")
    public void setLastrow(Boolean lastrow) {
        this.lastrow = lastrow;
    }

    @JsonProperty("result")
    public Result getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(Result result) {
        this.result = result;
    }

}
