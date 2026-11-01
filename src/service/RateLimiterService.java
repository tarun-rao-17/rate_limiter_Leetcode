package service;
import java.util.*;
import factory.RateLimiterFactory;
import enums.RateLimiterType;
import enums.UserTier;
import limiter.RateLimiter;
import models.RateLimitConfig;
import models.User; 


public class RateLimiterService {
    private final Map<UserTier,RateLimiter>rateLimiters=new HashMap<>();
    public RateLimiterService(){
        rateLimiters.put(UserTier.FREE,RateLimiterFactory.createRateLimiter(RateLimiterType.TOKEN_BUCKET,new RateLimitConfig(10,60)));
        rateLimiters.put(UserTier.PREMIUM,RateLimiterFactory.createRateLimiter(RateLimiterType.SLIDING_WINDOW_LOG,new RateLimitConfig(100,60)));
    }
    public boolean allowRequest(User user){
        RateLimiter rateLimiter=rateLimiters.get(user.getUserTier());
        if(rateLimiter==null){
            throw new IllegalArgumentException("No rate limiter configured for user tier: "+user.getUserTier());
        }
        return rateLimiter.allowRequest(user.getUserId());
    }
}
