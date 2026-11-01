package models;

public class RateLimitConfig {

    private final int maxRequests;
    private final int windowInseconds;

    // Constructor
    public RateLimitConfig(int maxRequests, int windowInseconds) {
        this.maxRequests = maxRequests;
        this.windowInseconds = windowInseconds;
    }

    // Getter for maxRequests
    public int getMaxRequests() {
        return maxRequests;
    }

    // Getter for windowInseconds
    public int getWindowInseconds() {
        return windowInseconds;
    }
}