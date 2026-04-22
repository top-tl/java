package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Site-wide totals returned by {@code GET /v1/stats}.
 *
 * <p>{@code total} is the overall count of listings; {@code channels},
 * {@code groups} and {@code bots} break that down by listing type.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalStats {

    @JsonProperty("total")
    private long total;

    @JsonProperty("channels")
    private long channels;

    @JsonProperty("groups")
    private long groups;

    @JsonProperty("bots")
    private long bots;

    public GlobalStats() {}

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getChannels() { return channels; }
    public void setChannels(long channels) { this.channels = channels; }

    public long getGroups() { return groups; }
    public void setGroups(long groups) { this.groups = groups; }

    public long getBots() { return bots; }
    public void setBots(long bots) { this.bots = bots; }

    @Override
    public String toString() {
        return "GlobalStats{total=" + total
                + ", channels=" + channels
                + ", groups=" + groups
                + ", bots=" + bots + "}";
    }
}
