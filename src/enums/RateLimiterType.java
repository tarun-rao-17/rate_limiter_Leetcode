package enums;
public enum RateLimiterType {
    FIXED_WINDOW,
    SLIDING_WINDOW,
    SLIDING_WINDOW_LOG,
    TOKEN_BUCKET,
    LEAKY_BUCKET
}