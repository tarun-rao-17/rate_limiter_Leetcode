package factory;
import enums.RateLimiterType;
import limiter.*;
import models.RateLimitConfig;
public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimiterType algo, RateLimitConfig config) {
        return switch (algo) {
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(config);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogRateLimiter(config);

            default -> throw new IllegalArgumentException("Unknown algorithm: " + algo);
        };
    }
}