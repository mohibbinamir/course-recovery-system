package crs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LoginLog implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String logId;
    private String userId;
    private String username;
    private String action;
    private LocalDateTime timestamp;
    private String ipAddress;
    private boolean success;
    
    public LoginLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    public LoginLog(String logId, String userId, String username, String action, boolean success) {
        this();
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.success = success;
    }
    
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s (%s)", 
            timestamp, userId, username, action, success ? "Success" : "Failed");
    }
}
