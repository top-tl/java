package tl.top.toptl;

import tl.top.toptl.models.StatsUpdate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Automatically posts stats to TOP.TL at a fixed interval.
 *
 * <pre>{@code
 * TopTL client = new TopTL("your-api-token");
 * TopTLAutoposter autoposter = new TopTLAutoposter(client, "mybot", () -> {
 *     return new StatsUpdate(bot.getGuilds().size(), null);
 * });
 * autoposter.start(30, TimeUnit.MINUTES);
 *
 * // Later, when shutting down:
 * autoposter.stop();
 * }</pre>
 */
public class TopTLAutoposter {

    private final TopTL client;
    private final String username;
    private final Supplier<StatsUpdate> statsSupplier;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> task;
    private OnError onError;

    @FunctionalInterface
    public interface OnError {
        void onError(Exception e);
    }

    /**
     * Creates a new autoposter.
     *
     * @param client        the TOP.TL client
     * @param username      the listing username to post stats for
     * @param statsSupplier a supplier that returns the current stats
     */
    public TopTLAutoposter(TopTL client, String username, Supplier<StatsUpdate> statsSupplier) {
        this.client = client;
        this.username = username;
        this.statsSupplier = statsSupplier;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "toptl-autoposter");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Sets an error handler for failed post attempts.
     */
    public TopTLAutoposter onError(OnError handler) {
        this.onError = handler;
        return this;
    }

    /**
     * Starts the autoposter with the given interval.
     *
     * @param interval the interval between posts
     * @param unit     the time unit for the interval
     */
    public synchronized void start(long interval, TimeUnit unit) {
        if (task != null) {
            throw new IllegalStateException("Autoposter is already running");
        }
        task = scheduler.scheduleAtFixedRate(this::post, 0, interval, unit);
    }

    /**
     * Stops the autoposter and shuts down the scheduler.
     */
    public synchronized void stop() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        scheduler.shutdown();
    }

    private void post() {
        try {
            StatsUpdate stats = statsSupplier.get();
            client.postStats(username, stats);
        } catch (Exception e) {
            if (onError != null) {
                onError.onError(e);
            }
        }
    }
}
