package tl.top.toptl.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoteCheck {

    @JsonProperty("hasVoted")
    private boolean hasVoted;

    @JsonProperty("timestamp")
    private String timestamp;

    public VoteCheck() {}

    public boolean isHasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "VoteCheck{hasVoted=" + hasVoted + ", timestamp='" + timestamp + "'}";
    }
}
