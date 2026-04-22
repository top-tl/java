package io.github.toptl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toptl.model.BatchStatsItem;
import io.github.toptl.model.GlobalStats;
import io.github.toptl.model.Listing;
import io.github.toptl.model.StatsPayload;
import io.github.toptl.model.VoteCheck;
import io.github.toptl.model.Voter;
import io.github.toptl.model.WebhookConfig;
import io.github.toptl.model.WebhookTestResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke tests for JSON (de)serialization across the model classes and a
 * minimal construction test for {@link TopTL} itself.
 */
class TopTLModelsTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void voteCheckAcceptsCanonicalAndLegacyKeys() throws Exception {
        VoteCheck canonical = mapper.readValue(
                "{\"voted\":true,\"votedAt\":\"2026-01-01T00:00:00Z\"}",
                VoteCheck.class);
        assertTrue(canonical.isVoted());
        assertEquals("2026-01-01T00:00:00Z", canonical.getVotedAt());

        VoteCheck legacy = mapper.readValue(
                "{\"hasVoted\":true,\"timestamp\":\"2026-01-02T00:00:00Z\"}",
                VoteCheck.class);
        assertTrue(legacy.isVoted());
        assertEquals("2026-01-02T00:00:00Z", legacy.getVotedAt());
    }

    @Test
    void globalStatsDecodesRealApiShape() throws Exception {
        GlobalStats s = mapper.readValue(
                "{\"total\":89000,\"channels\":42000,\"groups\":40000,\"bots\":7000}",
                GlobalStats.class);
        assertEquals(89000L, s.getTotal());
        assertEquals(42000L, s.getChannels());
        assertEquals(40000L, s.getGroups());
        assertEquals(7000L, s.getBots());
    }

    @Test
    void statsPayloadOmitsNullFields() throws Exception {
        StatsPayload p = new StatsPayload().memberCount(5000L);
        String json = mapper.writeValueAsString(p);
        assertTrue(json.contains("\"memberCount\":5000"));
        assertFalse(json.contains("groupCount"));
        assertFalse(json.contains("channelCount"));
        assertFalse(json.contains("botServes"));
    }

    @Test
    void statsPayloadIncludesAllFour() throws Exception {
        StatsPayload p = new StatsPayload()
                .memberCount(1L)
                .groupCount(2L)
                .channelCount(3L)
                .botServes(List.of("@alice"));
        String json = mapper.writeValueAsString(p);
        assertTrue(json.contains("\"memberCount\":1"));
        assertTrue(json.contains("\"groupCount\":2"));
        assertTrue(json.contains("\"channelCount\":3"));
        assertTrue(json.contains("\"botServes\":[\"@alice\"]"));
    }

    @Test
    void batchStatsItemRoundTrip() throws Exception {
        BatchStatsItem item = new BatchStatsItem("mybot").memberCount(100L);
        String json = mapper.writeValueAsString(item);
        assertTrue(json.contains("\"username\":\"mybot\""));
        assertTrue(json.contains("\"memberCount\":100"));
    }

    @Test
    void voterAcceptsIdAndCreatedAtAliases() throws Exception {
        Voter v = mapper.readValue(
                "{\"id\":\"42\",\"createdAt\":\"2026-01-01T00:00:00Z\",\"firstName\":\"Ada\"}",
                Voter.class);
        assertEquals("42", v.getUserId());
        assertEquals("2026-01-01T00:00:00Z", v.getVotedAt());
        assertEquals("Ada", v.getFirstName());
    }

    @Test
    void webhookConfigFallsBackToWebhookUrl() throws Exception {
        WebhookConfig c = mapper.readValue(
                "{\"webhookUrl\":\"https://example.com/hook\",\"rewardTitle\":\"Premium\"}",
                WebhookConfig.class);
        assertEquals("https://example.com/hook", c.getUrl());
        assertEquals("Premium", c.getRewardTitle());
    }

    @Test
    void webhookTestResultDecodes() throws Exception {
        WebhookTestResult r = mapper.readValue(
                "{\"success\":true,\"statusCode\":200,\"message\":\"ok\"}",
                WebhookTestResult.class);
        assertTrue(r.isSuccess());
        assertEquals(200, r.getStatusCode());
        assertEquals("ok", r.getMessage());
    }

    @Test
    void listingIgnoresUnknownFields() throws Exception {
        Listing l = mapper.readValue(
                "{\"username\":\"x\",\"title\":\"T\",\"type\":\"BOT\",\"voteCount\":5,\"newFieldFromServer\":123}",
                Listing.class);
        assertEquals("x", l.getUsername());
        assertEquals("T", l.getTitle());
        assertEquals(5L, l.getVoteCount());
    }

    @Test
    void clientRejectsEmptyApiKey() {
        assertThrows(IllegalArgumentException.class, () -> new TopTL(""));
    }

    @Test
    void clientBuildsWithBuilder() {
        TopTL c = TopTL.builder().apiKey("token").userAgent("mybot/1.0").build();
        assertNull(null);  // builder-only test: ensures no NPE during construction
        assertTrue(c != null);
    }

    @Test
    void statsPayloadRejectedWhenEmpty() {
        TopTL c = new TopTL("token");
        assertThrows(IllegalArgumentException.class,
                () -> c.postStats("mybot", new StatsPayload()));
    }
}
