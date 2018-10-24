
package ch.post.it.evoting.verifier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "_raw",
    "_time",
    "host",
    "index",
    "linecount",
    "source",
    "sourcetype",
    "splunk_server"
})
public class Result {

    @JsonProperty("_raw")
    private String raw;
    @JsonProperty("_time")
    private String time;
    @JsonProperty("host")
    private String host;
    @JsonProperty("index")
    private String index;
    @JsonProperty("linecount")
    private String linecount;
    @JsonProperty("source")
    private String source;
    @JsonProperty("sourcetype")
    private String sourcetype;
    @JsonProperty("splunk_server")
    private String splunkServer;

    @JsonProperty("_raw")
    public String getRaw() {
        return raw;
    }

    @JsonProperty("_raw")
    public void setRaw(String raw) {
        this.raw = raw;
    }

    @JsonProperty("_time")
    public String getTime() {
        return time;
    }

    @JsonProperty("_time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("linecount")
    public String getLinecount() {
        return linecount;
    }

    @JsonProperty("linecount")
    public void setLinecount(String linecount) {
        this.linecount = linecount;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("sourcetype")
    public String getSourcetype() {
        return sourcetype;
    }

    @JsonProperty("sourcetype")
    public void setSourcetype(String sourcetype) {
        this.sourcetype = sourcetype;
    }

    @JsonProperty("splunk_server")
    public String getSplunkServer() {
        return splunkServer;
    }

    @JsonProperty("splunk_server")
    public void setSplunkServer(String splunkServer) {
        this.splunkServer = splunkServer;
    }

}
