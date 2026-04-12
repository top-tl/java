# TOP.TL Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/tl.top/toptl)](https://central.sonatype.com/artifact/tl.top/toptl)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 11+](https://img.shields.io/badge/Java-11%2B-blue)](https://openjdk.org/)

Official Java SDK for the [TOP.TL](https://top.tl) Telegram Directory API.

## Installation

### Maven

```xml
<dependency>
    <groupId>tl.top</groupId>
    <artifactId>toptl</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'tl.top:toptl:1.0.0'
```

## Quick Start

```java
import tl.top.toptl.TopTL;
import tl.top.toptl.models.*;

TopTL client = new TopTL("your-api-token");

// Get listing info
Listing listing = client.getListing("mybot");
System.out.println(listing.getTitle() + " has " + listing.getVotes() + " votes");

// Get votes
VotesResponse votes = client.getVotes("mybot");
System.out.println("Total votes: " + votes.getTotal());

// Check if a user voted
VoteCheck check = client.hasVoted("mybot", "123456789");
if (check.isHasVoted()) {
    System.out.println("User has voted!");
}

// Post stats
client.postStats("mybot", new StatsUpdate(50000L, null));

// Get global stats
Stats stats = client.getStats();
System.out.println("Total listings on TOP.TL: " + stats.getTotalListings());
```

## Autoposter

Automatically post stats at a regular interval:

```java
import tl.top.toptl.TopTL;
import tl.top.toptl.TopTLAutoposter;
import tl.top.toptl.models.StatsUpdate;
import java.util.concurrent.TimeUnit;

TopTL client = new TopTL("your-api-token");

TopTLAutoposter autoposter = new TopTLAutoposter(client, "mybot", () -> {
    long memberCount = bot.getGuilds().stream()
            .mapToLong(g -> g.getMemberCount())
            .sum();
    return new StatsUpdate(memberCount, null);
});

autoposter.onError(e -> System.err.println("Failed to post stats: " + e.getMessage()));
autoposter.start(30, TimeUnit.MINUTES);

// When shutting down:
autoposter.stop();
```

## Requirements

- Java 11 or higher (uses `java.net.http.HttpClient`)

## License

[MIT](LICENSE) - TOP.TL
