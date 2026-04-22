package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from {@code GET /v1/listing/{username}/has-voted/{userId}}.
 *
 * <p>The canonical API keys are {@code voted} and {@code votedAt}. Older
 * server builds emitted {@code hasVoted} and {@code timestamp}; both are
 * accepted transparently via Jackson aliases.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoteCheck {

    @JsonProperty("voted")
    @JsonAlias({"hasVoted"})
    private boolean voted;

    @JsonProperty("votedAt")
    @JsonAlias({"timestamp"})
    private String votedAt;

    public VoteCheck() {}

    public boolean isVoted() { return voted; }
    public void setVoted(boolean voted) { this.voted = voted; }

    public String getVotedAt() { return votedAt; }
    public void setVotedAt(String votedAt) { this.votedAt = votedAt; }

    @Override
    public String toString() {
        return "VoteCheck{voted=" + voted + ", votedAt='" + votedAt + "'}";
    }
}
