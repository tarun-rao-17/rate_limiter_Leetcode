package limiter;

import java.util.*;

import enums.RateLimiterType;
import models.RateLimitConfig;

import java.util.concurrent.atomic.AtomicBoolean;

public class SlidingWindowLogRateLimiter extends RateLimiter {

    // Stores request timestamps separately for each user.
    // Key   -> userId
    // Value -> Queue containing timestamps of requests
    private final Map<String, Queue<Long>> requestLogs = new HashMap<>();

    // Constructor: receives the rate limit configuration
    public SlidingWindowLogRateLimiter(RateLimitConfig config) {

        // Calls the parent RateLimiter constructor.
        // Specifies that this limiter uses Sliding Window Log.
        super(config, RateLimiterType.SLIDING_WINDOW_LOG);
    }

    // Checks whether a particular user's request is allowed.
    // Returns true if allowed, false if rejected.
    @Override
    public boolean allowRequest(String userId) {

        // Initially, assume the request is not allowed.
        // AtomicBoolean allows us to modify the value inside the lambda.
        AtomicBoolean allowed = new AtomicBoolean(false);

        // Get the current timestamp in seconds.
        // System.currentTimeMillis() returns milliseconds,
        // so divide by 1000 to convert it into seconds.
        long now = System.currentTimeMillis() / 1000;

        // Get or update the request queue for this user.
        requestLogs.compute(userId, (id, log) -> {

            // If this is the user's first request,
            // create a new empty queue.
            if (log == null)
                log = new ArrayDeque<>();

            // Remove expired request timestamps.
            // If the age of the oldest request is greater than
            // or equal to the configured time window, remove it.
            //
            // Example:
            // now = 112
            // oldest timestamp = 100
            // window = 10
            //
            // 112 - 100 = 12
            // 12 >= 10, so remove timestamp 100.
            while (!log.isEmpty() &&
                   now - log.peek() >= config.getWindowInseconds()) {

                // Removes the oldest timestamp from the queue.
                log.poll();
            }

            // Check whether the number of requests in the current
            // window is below the maximum allowed request count.
            if (log.size() < config.getMaxRequests()) {

                // Allow the request and store its timestamp.
                log.add(now);

                // Mark the request as allowed.
                allowed.set(true);
            }

            // Return the updated queue to be stored in the map.
            return log;
        });

        // Return the final decision:
        // true  -> Request allowed
        // false -> Request rejected
        return allowed.get();
    }
}