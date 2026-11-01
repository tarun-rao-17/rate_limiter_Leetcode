package models;
import enums.UserTier;

public class User {

    private final String userId;
    private final UserTier userTier;

    // Constructor
    public User(String userId, UserTier userTier) {
        this.userId = userId;
        this.userTier = userTier;
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Getter for userTier
    public UserTier getUserTier() {
        return userTier;
    }
}