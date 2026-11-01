package limiter;

import enums.RateLimiterType;
import models.RateLimitConfig;

public abstract class RateLimiter {

    protected final RateLimitConfig config;
    protected final RateLimiterType type;

    // Constructor
    public RateLimiter(RateLimitConfig config,
                      RateLimiterType type) {

        this.config = config;
        this.type = type;
    }

    // Abstract method implemented by child classes
    public abstract boolean allowRequest(String userId);
}