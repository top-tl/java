package tl.top.toptl.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsUpdate {

    @JsonProperty("memberCount")
    private Long memberCount;

    @JsonProperty("groupCount")
    private Long groupCount;

    public StatsUpdate() {}

    public StatsUpdate(Long memberCount, Long groupCount) {
        this.memberCount = memberCount;
        this.groupCount = groupCount;
    }

    public static StatsUpdate withMemberCount(long memberCount) {
        return new StatsUpdate(memberCount, null);
    }

    public static StatsUpdate withGroupCount(long groupCount) {
        return new StatsUpdate(null, groupCount);
    }

    public Long getMemberCount() { return memberCount; }
    public void setMemberCount(Long memberCount) { this.memberCount = memberCount; }

    public Long getGroupCount() { return groupCount; }
    public void setGroupCount(Long groupCount) { this.groupCount = groupCount; }

    @Override
    public String toString() {
        return "StatsUpdate{memberCount=" + memberCount + ", groupCount=" + groupCount + "}";
    }
}
