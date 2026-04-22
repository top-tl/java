package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A user who voted for a listing.
 *
 * <p>The server sometimes uses {@code id} instead of {@code userId} for the
 * user identifier, and {@code createdAt} instead of {@code votedAt} for the
 * timestamp; both forms are accepted.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Voter {

    @JsonProperty("userId")
    @JsonAlias({"id"})
    private String userId = "";

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("username")
    private String username;

    @JsonProperty("votedAt")
    @JsonAlias({"createdAt"})
    private String votedAt;

    public Voter() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getVotedAt() { return votedAt; }
    public void setVotedAt(String votedAt) { this.votedAt = votedAt; }

    @Override
    public String toString() {
        return "Voter{userId='" + userId + "', username='" + username
                + "', votedAt='" + votedAt + "'}";
    }
}
