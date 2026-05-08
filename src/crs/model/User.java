package crs.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String userId;
    protected String username;
    protected String password;
    protected String email;
    protected String fullName;
    protected boolean isActive;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLogin;
    protected LocalDateTime lastLogout;
    
    public User() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String userId, String username, String password, String email, String fullName) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }
    
    public abstract String getRole();
    public abstract boolean hasPermission(String permission);
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public LocalDateTime getLastLogout() { return lastLogout; }
    public void setLastLogout(LocalDateTime lastLogout) { this.lastLogout = lastLogout; }
    
    public boolean authenticate(String password) {
        return this.password != null && this.password.equals(password) && this.isActive;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", fullName, username, getRole());
    }
}
