package tl.top.toptl;

import com.fasterxml.jackson.databind.ObjectMapper;
import tl.top.toptl.models.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Java client for the TOP.TL API.
 *
 * <pre>{@code
 * TopTL client = new TopTL("your-api-token");
 * Listing listing = client.getListing("durov");
 * }</pre>
 */
public class TopTL {

    private static final String DEFAULT_BASE_URL = "https://top.tl/api/v1";

    private final String token;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    /**
     * Creates a new TOP.TL client with the default base URL.
     *
     * @param token API bearer token
     */
    public TopTL(String token) {
        this(token, DEFAULT_BASE_URL);
    }

    /**
     * Creates a new TOP.TL client with a custom base URL.
     *
     * @param token   API bearer token
     * @param baseUrl custom base URL (e.g. for testing)
     */
    public TopTL(String token, String baseUrl) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("API token must not be null or empty");
        }
        this.token = token;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Gets listing information for the given username.
     */
    public Listing getListing(String username) throws IOException, InterruptedException {
        return get("/listing/" + username, Listing.class);
    }

    /**
     * Gets votes for the given listing.
     */
    public VotesResponse getVotes(String username) throws IOException, InterruptedException {
        return get("/listing/" + username + "/votes", VotesResponse.class);
    }

    /**
     * Checks whether a user has voted for a listing.
     */
    public VoteCheck hasVoted(String username, String userId) throws IOException, InterruptedException {
        return get("/listing/" + username + "/has-voted/" + userId, VoteCheck.class);
    }

    /**
     * Posts stats (member count, group count) for a listing.
     */
    public void postStats(String username, StatsUpdate stats) throws IOException, InterruptedException {
        String body = mapper.writeValueAsString(stats);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/listing/" + username + "/stats"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("User-Agent", "toptl-java/1.0.0")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("TOP.TL API error " + response.statusCode() + ": " + response.body());
        }
    }

    /**
     * Gets global TOP.TL statistics.
     */
    public Stats getStats() throws IOException, InterruptedException {
        return get("/stats", Stats.class);
    }

    private <T> T get(String path, Class<T> type) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", "toptl-java/1.0.0")
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("TOP.TL API error " + response.statusCode() + ": " + response.body());
        }
        return mapper.readValue(response.body(), type);
    }
}
