package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A single entry in {@code POST /v1/stats/batch}.
 *
 * <p>Same shape as {@link StatsPayload} plus a required {@code username}.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchStatsItem {

    @JsonProperty("username")
    private String username;

    @JsonProperty("memberCount")
    private Long memberCount;

    @JsonProperty("groupCount")
    private Long groupCount;

    @JsonProperty("channelCount")
    private Long channelCount;

    @JsonProperty("botServes")
    private List<String> botServes;

    public BatchStatsItem() {}

    public BatchStatsItem(String username) {
        this.username = username;
    }

    public BatchStatsItem username(String username) {
        this.username = username;
        return this;
    }

    public BatchStatsItem memberCount(Long memberCount) {
        this.memberCount = memberCount;
        return this;
    }

    public BatchStatsItem groupCount(Long groupCount) {
        this.groupCount = groupCount;
        return this;
    }

    public BatchStatsItem channelCount(Long channelCount) {
        this.channelCount = channelCount;
        return this;
    }

    public BatchStatsItem botServes(List<String> botServes) {
        this.botServes = botServes;
        return this;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getMemberCount() { return memberCount; }
    public void setMemberCount(Long memberCount) { this.memberCount = memberCount; }

    public Long getGroupCount() { return groupCount; }
    public void setGroupCount(Long groupCount) { this.groupCount = groupCount; }

    public Long getChannelCount() { return channelCount; }
    public void setChannelCount(Long channelCount) { this.channelCount = channelCount; }

    public List<String> getBotServes() { return botServes; }
    public void setBotServes(List<String> botServes) { this.botServes = botServes; }

    @Override
    public String toString() {
        return "BatchStatsItem{username='" + username
                + "', memberCount=" + memberCount
                + ", groupCount=" + groupCount
                + ", channelCount=" + channelCount
                + ", botServes=" + botServes + "}";
    }
}
