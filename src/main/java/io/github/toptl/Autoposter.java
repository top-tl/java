package io.github.toptl;

import io.github.toptl.model.StatsPayload;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Background scheduler that calls {@link TopTL#postStats(String, StatsPayload)}
 * on a fixed interval.
 *
 * <p>Designed for long-running bot processes. For cron-style one-shots, just
 * call {@code client.postStats(...)} directly.</p>
 *
 * <pre>{@code
 * TopTL client = new TopTL("toptl_xxx");
 * Autoposter poster = new Autoposter(client, "mybot", () ->
 *     new StatsPayload().memberCount((long) bot.getUserCount()));
 * poster.onError(e -> log.warn("toptl post failed: {}", e.getMessage()));
 * poster.start(30, TimeUnit.MINUTES);
 * ...
 * poster.stop();
 * }</pre>
 */
public class Autoposter {

    private final TopTL client;
    private final String username;
    private final Supplier<StatsPayload> supplier;
    private final ScheduledExecutorService scheduler;
    private final boolean ownsScheduler;
    private ScheduledFuture<?> task;
    private Consumer<Throwable> onError;
    private volatile StatsPayload lastPayload;
    private boolean onlyOnChange;

    public Autoposter(TopTL client, String username, Supplier<StatsPayload> supplier) {
        this(client, username, supplier, null);
    }

    /**
     * @param scheduler optional executor; if {@code null} the autoposter creates
     *                  and owns a single-thread daemon scheduler.
     */
    public Autoposter(
            TopTL client,
            String username,
            Supplier<StatsPayload> supplier,
            ScheduledExecutorService scheduler) {
        this.client = Objects.requireNonNull(client, "client");
        this.username = Objects.requireNonNull(username, "username");
        this.supplier = Objects.requireNonNull(supplier, "supplier");
        if (scheduler != null) {
            this.scheduler = scheduler;
            this.ownsScheduler = false;
        } else {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "toptl-autopost:" + username);
                t.setDaemon(true);
                return t;
            });
            this.ownsScheduler = true;
        }
    }

    /** Handler invoked when a tick throws. By default the exception is swallowed. */
    public Autoposter onError(Consumer<Throwable> handler) {
        this.onError = handler;
        return this;
    }

    /** Skip posts when the new payload is deep-equal to the previous one. */
    public Autoposter onlyOnChange(boolean onlyOnChange) {
        this.onlyOnChange = onlyOnChange;
        return this;
    }

    public synchronized void start(long interval, TimeUnit unit) {
        if (task != null && !task.isCancelled()) {
            throw new IllegalStateException("Autoposter is already running");
        }
        task = scheduler.scheduleAtFixedRate(this::tick, 0, interval, unit);
    }

    public synchronized void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        if (ownsScheduler) {
            scheduler.shutdown();
        }
    }

    /** Run one cycle synchronously — useful from cron-driven environments. */
    public void postOnce() {
        tick();
    }

    private void tick() {
        StatsPayload payload;
        try {
            payload = supplier.get();
        } catch (Throwable t) {
            dispatchError(t);
            return;
        }
        if (payload == null || payload.isEmpty()) {
            return;
        }
        if (onlyOnChange && equalPayloads(payload, lastPayload)) {
            return;
        }
        try {
            client.postStats(username, payload);
            lastPayload = payload;
        } catch (Throwable t) {
            dispatchError(t);
        }
    }

    private static boolean equalPayloads(StatsPayload a, StatsPayload b) {
        if (a == null || b == null) {
            return a == b;
        }
        return Objects.equals(a.getMemberCount(), b.getMemberCount())
                && Objects.equals(a.getGroupCount(), b.getGroupCount())
                && Objects.equals(a.getChannelCount(), b.getChannelCount())
                && Objects.equals(a.getBotServes(), b.getBotServes());
    }

    private void dispatchError(Throwable t) {
        if (onError != null) {
            try {
                onError.accept(t);
            } catch (Throwable ignored) {
                // never let the error handler break the scheduler
            }
        }
    }
}
