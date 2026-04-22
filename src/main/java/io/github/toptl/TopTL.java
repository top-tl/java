package io.github.toptl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.toptl.exception.AuthenticationException;
import io.github.toptl.exception.NotFoundException;
import io.github.toptl.exception.RateLimitException;
import io.github.toptl.exception.TopTLException;
import io.github.toptl.exception.ValidationException;
import io.github.toptl.model.BatchStatsItem;
import io.github.toptl.model.GlobalStats;
import io.github.toptl.model.Listing;
import io.github.toptl.model.StatsPayload;
import io.github.toptl.model.StatsResult;
import io.github.toptl.model.VoteCheck;
import io.github.toptl.model.Voter;
import io.github.toptl.model.WebhookConfig;
import io.github.toptl.model.WebhookTestResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Synchronous client for the TOP.TL public API.
 *
 * <p>Built on {@link java.net.http.HttpClient} (JDK stdlib) — no third-party
 * HTTP dependency. All operations are blocking; wire them to whatever
 * thread-pool / scheduler your bot already uses.</p>
 *
 * <pre>{@code
 * TopTL client = new TopTL("toptl_xxx");
 * Listing listing = client.getListing("mybot");
 * VoteCheck check = client.hasVoted("mybot", "123456789");
 * if (check.isVoted()) {
 *     grantPremium(123456789L);
 * }
 * }</pre>
 *
 * <p>Failures throw a subclass of {@link TopTLException} — see
 * {@link AuthenticationException}, {@link NotFoundException},
 * {@link RateLimitException} and {@link ValidationException}.</p>
 */
public class TopTL {

    /** Default base URL — {@code https://top.tl/api}. */
    public static final String DEFAULT_BASE_URL = "https://top.tl/api";

    /** SDK version reported in the {@code User-Agent}. */
    public static final String SDK_VERSION = "0.1.0";

    private static final String DEFAULT_USER_AGENT = "toptl-java/" + SDK_VERSION;

    private final String apiKey;
    private final String baseUrl;
    private final String userAgent;
    private final Duration timeout;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    /** Creates a client with the default base URL and a 15s timeout. */
    public TopTL(String apiKey) {
        this(builder().apiKey(apiKey));
    }

    private TopTL(Builder b) {
        if (b.apiKey == null || b.apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey is required");
        }
        this.apiKey = b.apiKey;
        String url = b.baseUrl == null ? DEFAULT_BASE_URL : b.baseUrl;
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        this.timeout = b.timeout == null ? Duration.ofSeconds(15) : b.timeout;
        this.userAgent = b.userAgent == null
                ? DEFAULT_USER_AGENT
                : DEFAULT_USER_AGENT + " " + b.userAgent;
        this.httpClient = b.httpClient != null
                ? b.httpClient
                : HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Builder builder() {
        return new Builder();
    }

    // ------------------------------------------------------------------
    // Public methods — one per API endpoint
    // ------------------------------------------------------------------

    /** {@code GET /v1/listing/{username}} — fetch a listing by its Telegram username. */
    public Listing getListing(String username) {
        JsonNode data = request("GET", "/v1/listing/" + encode(username), null);
        return mapper.convertValue(data, Listing.class);
    }

    /** {@code GET /v1/listing/{username}/votes} — recent voters (most recent first), default limit 20. */
    public List<Voter> getVotes(String username) {
        return getVotes(username, 20);
    }

    /** {@code GET /v1/listing/{username}/votes?limit=N} — accepts both bare-array and {@code {items: [...]}} responses. */
    public List<Voter> getVotes(String username, int limit) {
        JsonNode data = request(
                "GET",
                "/v1/listing/" + encode(username) + "/votes?limit=" + limit,
                null);
        JsonNode items = data;
        if (data != null && data.isObject() && data.has("items")) {
            items = data.get("items");
        }
        if (items == null || !items.isArray()) {
            return Collections.emptyList();
        }
        List<Voter> out = new ArrayList<>(items.size());
        for (JsonNode n : items) {
            out.add(mapper.convertValue(n, Voter.class));
        }
        return out;
    }

    /** {@code GET /v1/listing/{username}/has-voted/{userId}} — has the Telegram user voted for this listing? */
    public VoteCheck hasVoted(String username, String userId) {
        JsonNode data = request(
                "GET",
                "/v1/listing/" + encode(username) + "/has-voted/" + encode(userId),
                null);
        return mapper.convertValue(data, VoteCheck.class);
    }

    /** Convenience overload — accepts a numeric Telegram user id. */
    public VoteCheck hasVoted(String username, long userId) {
        return hasVoted(username, Long.toString(userId));
    }

    /** {@code POST /v1/listing/{username}/stats} — update counters on a listing you own. */
    public StatsResult postStats(String username, StatsPayload payload) {
        Objects.requireNonNull(payload, "payload");
        if (payload.isEmpty()) {
            throw new IllegalArgumentException(
                    "StatsPayload needs at least one of memberCount, groupCount, "
                            + "channelCount, or botServes");
        }
        JsonNode data = request(
                "POST",
                "/v1/listing/" + encode(username) + "/stats",
                payload);
        return convertOrEmpty(data, StatsResult.class);
    }

    /** {@code POST /v1/stats/batch} — post stats for up to 25 listings in a single request. */
    public List<StatsResult> batchPostStats(List<BatchStatsItem> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        JsonNode data = request("POST", "/v1/stats/batch", items);
        if (data == null || !data.isArray()) {
            return Collections.emptyList();
        }
        try {
            return mapper.convertValue(data, new TypeReference<List<StatsResult>>() {});
        } catch (IllegalArgumentException e) {
            throw new TopTLException("Failed to decode batch response: " + e.getMessage(), e);
        }
    }

    /** {@code PUT /v1/listing/{username}/webhook} — register or replace the listing's vote webhook. */
    public WebhookConfig setWebhook(String username, String url) {
        return setWebhook(username, url, null);
    }

    /** Overload accepting a {@code rewardTitle} shown to the voter in their notification. */
    public WebhookConfig setWebhook(String username, String url, String rewardTitle) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("webhook url is required");
        }
        WebhookConfig body = new WebhookConfig(url, rewardTitle);
        JsonNode data = request(
                "PUT",
                "/v1/listing/" + encode(username) + "/webhook",
                body);
        WebhookConfig result = mapper.convertValue(data, WebhookConfig.class);
        if (result == null) {
            result = new WebhookConfig(url, rewardTitle);
        }
        return result;
    }

    /** {@code POST /v1/listing/{username}/webhook/test} — send a synthetic vote event to the configured webhook. */
    public WebhookTestResult testWebhook(String username) {
        JsonNode data = request(
                "POST",
                "/v1/listing/" + encode(username) + "/webhook/test",
                null);
        return convertOrEmpty(data, WebhookTestResult.class);
    }

    /** {@code GET /v1/stats} — site-wide totals. */
    public GlobalStats getGlobalStats() {
        JsonNode data = request("GET", "/v1/stats", null);
        return convertOrEmpty(data, GlobalStats.class);
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private <T> T convertOrEmpty(JsonNode data, Class<T> type) {
        T out = mapper.convertValue(data, type);
        if (out != null) {
            return out;
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new TopTLException("Cannot instantiate " + type.getSimpleName(), e);
        }
    }

    private JsonNode request(String method, String path, Object body) {
        HttpRequest.Builder rb = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + apiKey)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .timeout(timeout);

        HttpRequest.BodyPublisher pub;
        if (body == null) {
            pub = HttpRequest.BodyPublishers.noBody();
        } else {
            try {
                String json = mapper.writeValueAsString(body);
                pub = HttpRequest.BodyPublishers.ofString(json);
                rb.header("Content-Type", "application/json");
            } catch (IOException e) {
                throw new TopTLException("Failed to serialize request body: " + e.getMessage(), e);
            }
        }
        rb.method(method, pub);

        HttpResponse<String> response;
        try {
            response = httpClient.send(rb.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new TopTLException("HTTP request failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TopTLException("HTTP request was interrupted", e);
        }

        int status = response.statusCode();
        String respBody = response.body();

        if (status >= 200 && status < 300) {
            if (respBody == null || respBody.isEmpty()) {
                return null;
            }
            String ct = response.headers().firstValue("content-type").orElse("");
            if (!ct.toLowerCase().contains("json")) {
                return null;
            }
            try {
                return mapper.readTree(respBody);
            } catch (IOException e) {
                throw new TopTLException("Failed to parse response JSON: " + e.getMessage(),
                        status, respBody, e);
            }
        }

        String message = extractErrorMessage(respBody);
        String msg = "TOP.TL API error " + status + ": " + message;
        switch (status) {
            case 401:
            case 403:
                throw new AuthenticationException(msg, status, respBody);
            case 404:
                throw new NotFoundException(msg, status, respBody);
            case 429:
                throw new RateLimitException(msg, status, respBody);
            default:
                if (status >= 400 && status < 500) {
                    throw new ValidationException(msg, status, respBody);
                }
                throw new TopTLException(msg, status, respBody);
        }
    }

    private String extractErrorMessage(String body) {
        if (body == null || body.isEmpty()) {
            return "(empty response)";
        }
        try {
            JsonNode n = mapper.readTree(body);
            if (n.isObject()) {
                JsonNode err = n.get("error");
                if (err != null && err.isTextual()) {
                    return err.asText();
                }
                JsonNode msg = n.get("message");
                if (msg != null && msg.isTextual()) {
                    return msg.asText();
                }
            }
        } catch (IOException ignored) {
            // fall through
        }
        return body;
    }

    private static String encode(String s) {
        // API segments are already safe (usernames, numeric ids), but defend
        // against leading '@' which users sometimes include.
        if (s == null) {
            return "";
        }
        return s.startsWith("@") ? s.substring(1) : s;
    }

    // ------------------------------------------------------------------
    // Builder
    // ------------------------------------------------------------------

    /** Fluent builder for {@link TopTL}. Use {@link TopTL#builder()}. */
    public static final class Builder {
        private String apiKey;
        private String baseUrl;
        private String userAgent;
        private Duration timeout;
        private HttpClient httpClient;

        private Builder() {}

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public Builder timeout(Duration timeout) { this.timeout = timeout; return this; }
        public Builder httpClient(HttpClient httpClient) { this.httpClient = httpClient; return this; }

        public TopTL build() { return new TopTL(this); }
    }
}
