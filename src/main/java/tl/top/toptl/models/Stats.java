package tl.top.toptl.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    @JsonProperty("totalListings")
    private Long totalListings;

    @JsonProperty("totalVotes")
    private Long totalVotes;

    @JsonProperty("totalUsers")
    private Long totalUsers;

    public Stats() {}

    public Long getTotalListings() { return totalListings; }
    public void setTotalListings(Long totalListings) { this.totalListings = totalListings; }

    public Long getTotalVotes() { return totalVotes; }
    public void setTotalVotes(Long totalVotes) { this.totalVotes = totalVotes; }

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    @Override
    public String toString() {
        return "Stats{totalListings=" + totalListings + ", totalVotes=" + totalVotes + ", totalUsers=" + totalUsers + "}";
    }
}
