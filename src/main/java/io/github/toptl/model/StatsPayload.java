package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Body of {@code POST /v1/listing/{username}/stats}.
 *
 * <p>Only non-null fields are serialized — the server keeps existing values
 * for any counter you don't pass.</p>
 *
 * <pre>{@code
 * StatsPayload payload = new StatsPayload()
 *     .memberCount(5_000L)
 *     .channelCount(42L)
 *     .botServes(List.of("@alice", "@bob"));
 * client.postStats("mybot", payload);
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsPayload {

    @JsonProperty("memberCount")
    private Long memberCount;

    @JsonProperty("groupCount")
    private Long groupCount;

    @JsonProperty("channelCount")
    private Long channelCount;

    @JsonProperty("botServes")
    private List<String> botServes;

    public StatsPayload() {}

    public StatsPayload memberCount(Long memberCount) {
        this.memberCount = memberCount;
        return this;
    }

    public StatsPayload groupCount(Long groupCount) {
        this.groupCount = groupCount;
        return this;
    }

    public StatsPayload channelCount(Long channelCount) {
        this.channelCount = channelCount;
        return this;
    }

    public StatsPayload botServes(List<String> botServes) {
        this.botServes = botServes;
        return this;
    }

    public Long getMemberCount() { return memberCount; }
    public void setMemberCount(Long memberCount) { this.memberCount = memberCount; }

    public Long getGroupCount() { return groupCount; }
    public void setGroupCount(Long groupCount) { this.groupCount = groupCount; }

    public Long getChannelCount() { return channelCount; }
    public void setChannelCount(Long channelCount) { this.channelCount = channelCount; }

    public List<String> getBotServes() { return botServes; }
    public void setBotServes(List<String> botServes) { this.botServes = botServes; }

    /** True when this payload carries no counters — callers should skip the HTTP request. */
    public boolean isEmpty() {
        return memberCount == null
                && groupCount == null
                && channelCount == null
                && (botServes == null || botServes.isEmpty());
    }

    @Override
    public String toString() {
        return "StatsPayload{memberCount=" + memberCount
                + ", groupCount=" + groupCount
                + ", channelCount=" + channelCount
                + ", botServes=" + botServes + "}";
    }
}
