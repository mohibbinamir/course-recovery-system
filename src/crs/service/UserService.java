package crs.service;

import crs.model.*;
import crs.util.FileManager;
import crs.util.PasswordUtil;
import crs.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    
    private static final String USERS_FILE = "users.dat";
    private static final String LOGIN_LOGS_FILE = "login_logs.dat";
    
    private List<User> users;
    private List<LoginLog> loginLogs;
    private User currentUser;
    
    private static UserService instance;
    
    private UserService() {
        loadUsers();
        loadLoginLogs();
        initializeDefaultUsers();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    private void loadUsers() {
        users = FileManager.loadFromBinaryFile(USERS_FILE);
        if (users == null) {
            users = new ArrayList<>();
        }
    }
    
    private void loadLoginLogs() {
        loginLogs = FileManager.loadFromBinaryFile(LOGIN_LOGS_FILE);
        if (loginLogs == null) {
            loginLogs = new ArrayList<>();
        }
    }
    
    private void saveUsers() {
        FileManager.saveToBinaryFile(USERS_FILE, users);
    }
    
    private void saveLoginLogs() {
        FileManager.saveToBinaryFile(LOGIN_LOGS_FILE, loginLogs);
    }
    
    private void initializeDefaultUsers() {
        if (users.isEmpty()) {
            AcademicOfficer admin = new AcademicOfficer(
                "U001", "admin", PasswordUtil.hashPassword("admin123"),
                "admin@university.edu", "System Administrator"
            );
            admin.setDepartment("Administration");
            users.add(admin);
            
            CourseAdministrator courseAdmin = new CourseAdministrator(
                "U002", "courseadmin", PasswordUtil.hashPassword("course123"),
                "courseadmin@university.edu", "Course Administrator"
            );
            courseAdmin.setAssignedProgram("Computer Science");
            users.add(courseAdmin);
            
            saveUsers();
        }
    }
    
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.isActive()) {
                String hashedPassword = PasswordUtil.hashPassword(password);
                if (user.getPassword().equals(hashedPassword)) {
                    user.setLastLogin(LocalDateTime.now());
                    currentUser = user;
                    logLogin(user, "LOGIN", true);
                    saveUsers();
                    return user;
                }
            }
        }
        logLogin(null, "LOGIN", false);
        return null;
    }
    
    public void logout() {
        if (currentUser != null) {
            currentUser.setLastLogout(LocalDateTime.now());
            logLogin(currentUser, "LOGOUT", true);
            saveUsers();
            currentUser = null;
        }
    }
    
    private void logLogin(User user, String action, boolean success) {
        LoginLog log = new LoginLog(
            generateId(),
            user != null ? user.getUserId() : "UNKNOWN",
            user != null ? user.getUsername() : "UNKNOWN",
            action,
            success
        );
        loginLogs.add(log);
        saveLoginLogs();
    }
    
    public User createUser(String username, String password, String email, String fullName, String role) {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username format");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user;
        String userId = generateId();
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        if ("Academic Officer".equals(role)) {
            user = new AcademicOfficer(userId, username, hashedPassword, email, fullName);
        } else {
            user = new CourseAdministrator(userId, username, hashedPassword, email, fullName);
        }
        
        users.add(user);
        saveUsers();
        return user;
    }
    
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(user.getUserId())) {
                users.set(i, user);
                saveUsers();
                return;
            }
        }
    }
    
    public void deactivateUser(String userId) {
        User user = findById(userId);
        if (user != null) {
            user.setActive(false);
            saveUsers();
        }
    }
    
    public void activateUser(String userId) {
        User user = findById(userId);
        if (user != null) {
            user.setActive(true);
            saveUsers();
        }
    }
    
    public String resetPassword(String userId) {
        User user = findById(userId);
        if (user != null) {
            String newPassword = PasswordUtil.generateTemporaryPassword();
            user.setPassword(PasswordUtil.hashPassword(newPassword));
            saveUsers();
            return newPassword;
        }
        return null;
    }
    
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        if (user != null) {
            String hashedOld = PasswordUtil.hashPassword(oldPassword);
            if (user.getPassword().equals(hashedOld)) {
                user.setPassword(PasswordUtil.hashPassword(newPassword));
                saveUsers();
                return true;
            }
        }
        return false;
    }
    
    public User findById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public User findByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    public List<User> getActiveUsers() {
        List<User> active = new ArrayList<>();
        for (User user : users) {
            if (user.isActive()) {
                active.add(user);
            }
        }
        return active;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public List<LoginLog> getLoginLogs() {
        return new ArrayList<>(loginLogs);
    }
    
    public List<LoginLog> getLoginLogsForUser(String userId) {
        List<LoginLog> userLogs = new ArrayList<>();
        for (LoginLog log : loginLogs) {
            if (log.getUserId().equals(userId)) {
                userLogs.add(log);
            }
        }
        return userLogs;
    }
    
    private String generateId() {
        return "U" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
