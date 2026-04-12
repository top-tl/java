package tl.top.toptl.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VotesResponse {

    @JsonProperty("votes")
    private List<Vote> votes;

    @JsonProperty("total")
    private Long total;

    public VotesResponse() {}

    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    @Override
    public String toString() {
        return "VotesResponse{total=" + total + ", votes=" + (votes != null ? votes.size() : 0) + "}";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vote {

        @JsonProperty("userId")
        private String userId;

        @JsonProperty("timestamp")
        private String timestamp;

        public Vote() {}

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "Vote{userId='" + userId + "', timestamp='" + timestamp + "'}";
        }
    }
}
