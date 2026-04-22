# toptl

[![Maven Central](https://img.shields.io/maven-central/v/io.github.top-tl/toptl.svg?label=maven%20central&color=3775a9)](https://central.sonatype.com/artifact/io.github.top-tl/toptl)
[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg?color=f89820)](https://openjdk.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](./LICENSE)
[![TOP.TL](https://img.shields.io/badge/top.tl-developers-2ec4b6)](https://top.tl/developers)

The official Java SDK for **TOP.TL** — post stats, check votes, and manage vote webhooks for your Telegram bot, channel, or group listed on [top.tl](https://top.tl).

## Install

### Maven

```xml
<dependency>
    <groupId>io.github.top-tl</groupId>
    <artifactId>toptl</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation "io.github.top-tl:toptl:0.1.0"
```

Requires Java 17+. Depends only on `jackson-databind`; HTTP uses the JDK's built-in `java.net.http.HttpClient`.

## Quickstart

Get an API key at https://top.tl/profile → **API Keys**. Scope the key to your listing and the operations you need (`listing:read`, `listing:write`, `votes:read`, `votes:check`).

```java
import io.github.toptl.TopTL;
import io.github.toptl.model.*;

TopTL client = new TopTL("toptl_xxx");

// Fetch a listing
Listing listing = client.getListing("mybot");
System.out.println(listing.getTitle() + " — " + listing.getVoteCount() + " votes");

// Post stats on a listing you own
client.postStats("mybot", new StatsPayload()
    .memberCount(5_000L)
    .groupCount(1_200L)
    .channelCount(300L));

// Reward users who voted
VoteCheck check = client.hasVoted("mybot", 123456789L);
if (check.isVoted()) {
    grantPremium(123456789L);
}
```

## Autoposter

For long-running bot processes, the SDK ships with a background autoposter that calls `postStats` on an interval and only hits the API when the stats actually changed:

```java
import io.github.toptl.Autoposter;
import io.github.toptl.TopTL;
import io.github.toptl.model.StatsPayload;
import java.util.concurrent.TimeUnit;

TopTL client = new TopTL("toptl_xxx");
Autoposter poster = new Autoposter(client, "mybot",
    () -> new StatsPayload().memberCount((long) bot.getUserCount()))
    .onlyOnChange(true)
    .onError(e -> log.warn("toptl post failed: {}", e.getMessage()));

poster.start(30, TimeUnit.MINUTES);
// ...
poster.stop();
```

For cron-style one-shots, skip the autoposter and call `client.postStats(...)` directly.

## Vote webhooks

Register a URL TOP.TL will POST to whenever someone votes for your listing:

```java
client.setWebhook("mybot", "https://mybot.example.com/toptl-vote", "30-day premium");

WebhookTestResult result = client.testWebhook("mybot");
System.out.println(result.isSuccess() + " " + result.getStatusCode());
```

## Batch stats

Post stats for up to 25 listings in one request:

```java
import java.util.List;

client.batchPostStats(List.of(
    new BatchStatsItem("bot1").memberCount(1_200L),
    new BatchStatsItem("bot2").memberCount(5_400L)
));
```

## Error handling

Every API error throws a subclass of `TopTLException`:

```java
import io.github.toptl.exception.*;

try {
    client.postStats("mybot", new StatsPayload().memberCount(5_000L));
} catch (AuthenticationException e) {
    // bad key, or missing scope
} catch (NotFoundException e) {
    // listing does not exist
} catch (RateLimitException e) {
    // back off and retry
} catch (ValidationException e) {
    // payload rejected — inspect e.getResponseBody()
}
```

## Telegram bot integration

Using [rubenlagus/TelegramBots](https://github.com/rubenlagus/TelegramBots)? Add the [plugin](https://github.com/top-tl/java-telegram-bot) for drop-in auto-posting and vote checks.

## License

MIT — see [LICENSE](./LICENSE).
