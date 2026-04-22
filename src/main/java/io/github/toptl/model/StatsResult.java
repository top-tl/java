package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response from {@code POST /v1/listing/{username}/stats} and per-item
 * response from {@code POST /v1/stats/batch}.
 *
 * <p>On batch failures individual items still appear here with
 * {@code success=false} and an {@code error} string.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsResult {

    @JsonProperty("success")
    private boolean success = true;

    @JsonProperty("username")
    private String username;

    @JsonProperty("error")
    private String error;

    public StatsResult() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    @Override
    public String toString() {
        return "StatsResult{success=" + success
                + ", username='" + username
                + "', error='" + error + "'}";
    }
}
