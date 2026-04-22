package io.github.toptl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * A TOP.TL listing — a bot, channel, or group registered on the directory.
 *
 * <p>Unknown fields returned by the server are silently ignored so the SDK
 * doesn't break when the API adds new keys.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Listing {

    @JsonProperty("id")
    private String id = "";

    @JsonProperty("username")
    private String username = "";

    @JsonProperty("title")
    private String title = "";

    /** Listing type: {@code "CHANNEL"}, {@code "GROUP"} or {@code "BOT"}. */
    @JsonProperty("type")
    private String type = "";

    @JsonProperty("description")
    private String description;

    @JsonProperty("memberCount")
    private long memberCount;

    @JsonProperty("voteCount")
    private long voteCount;

    @JsonProperty("languages")
    private List<String> languages = new ArrayList<>();

    @JsonProperty("verified")
    private boolean verified;

    @JsonProperty("featured")
    private boolean featured;

    @JsonProperty("photoUrl")
    private String photoUrl;

    public Listing() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getMemberCount() { return memberCount; }
    public void setMemberCount(long memberCount) { this.memberCount = memberCount; }

    public long getVoteCount() { return voteCount; }
    public void setVoteCount(long voteCount) { this.voteCount = voteCount; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) {
        this.languages = languages == null ? new ArrayList<>() : languages;
    }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    @Override
    public String toString() {
        return "Listing{username='" + username + "', title='" + title
                + "', type='" + type + "', voteCount=" + voteCount + "}";
    }
}
