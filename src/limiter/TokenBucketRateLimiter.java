package limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import enums.RateLimiterType;
import models.RateLimitConfig;

import java.util.concurrent.atomic.AtomicBoolean;

public class TokenBucketRateLimiter extends RateLimiter {

    // Stores the number of available tokens for each user.
    // Key   -> userId
    // Value -> current available tokens
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

    // Stores the last time tokens were refilled for each user.
    // Key   -> userId
    // Value -> timestamp in milliseconds
    private final Map<String, Long> lastRefillTime = new ConcurrentHashMap<>();

    // Constructor: receives the rate limit configuration.
    public TokenBucketRateLimiter(RateLimitConfig config) {

        // Calls the parent RateLimiter constructor.
        // Specifies that this limiter uses the Token Bucket algorithm.
        super(config, RateLimiterType.TOKEN_BUCKET);
    }

    // Checks whether a user's request is allowed.
    // Returns true if allowed, false if rejected.
    @Override
    public boolean allowRequest(String userId) {

        // Initially assume the request is not allowed.
        AtomicBoolean allowed = new AtomicBoolean(false);

        // Atomically calculate and update the token count for this user.
        tokens.compute(userId, (id, availableTokens) -> {

            // Refill tokens based on the elapsed time.
            // System.currentTimeMillis() gives the current time in milliseconds.
            int currentTokens = refillTokens(userId, System.currentTimeMillis());

            // Check whether at least one token is available.
            if (currentTokens > 0) {

                // A token is available, so allow the request.
                allowed.set(true);

                // Consume one token for this request.
                // Return the updated token count to the map.
                return currentTokens - 1;
            }

            // No tokens available.
            // Return the current token count without consuming anything.
            return currentTokens;
        });

        // Return the final decision:
        // true  -> Request allowed
        // false -> Request rejected
        return allowed.get();
    }

    // Calculates how many tokens should be refilled
    // based on the time elapsed since the last refill.

    // Example:
    // maxRequests = 10
    // windowInSeconds = 60
    //
    // Refill rate = 60 / 10 = 6 seconds per token
    //
    // Tokens are replenished at a rate of
    // 1 token every 6 seconds.

    private int refillTokens(String userId, long now) {

        // Calculate how many seconds are required to refill one token.
        //
        // Example:
        // windowInSeconds = 60
        // maxRequests = 10
        //
        // refillRate = 60 / 10 = 6 seconds per token
        double refillRate =
                (double) config.getWindowInseconds()
                / config.getMaxRequests();

        // If this is the user's first request,
        // initialize their last refill time.
        lastRefillTime.putIfAbsent(userId, now);

        // Get the timestamp of the user's last refill.
        long lastRefill = lastRefillTime.get(userId);

        // Calculate how many whole seconds have elapsed
        // since the last refill.
        //
        // Note: now and lastRefill are in milliseconds.
        // Divide by 1000 to convert milliseconds to seconds.
        long elapsed = (now - lastRefill) / 1000;

        // Calculate how many tokens can be refilled.
        //
        // Example:
        // elapsed = 12 seconds
        // refillRate = 6 seconds per token
        //
        // refillToken = 12 / 6 = 2 tokens
        int refillToken = (int) (elapsed / refillRate);

        // Get the user's current token count.
        //
        // If the user is new, start with the maximum number
        // of tokens available in the bucket.
        int currentTokens =
                tokens.getOrDefault(userId, config.getMaxRequests());

        // Add the newly refilled tokens.
        //
        // Math.min() ensures the bucket never contains
        // more tokens than the configured maximum.
        currentTokens = Math.min(
                currentTokens + refillToken,
                config.getMaxRequests()
        );

        // If at least one token was refilled,
        // update the last refill timestamp.
        if (refillToken > 0)
            lastRefillTime.put(userId, now);

        // Return the updated token count.
        return currentTokens;
    }
}