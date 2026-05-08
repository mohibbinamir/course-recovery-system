package crs.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{4,20}$"
    );
    
    private static final Pattern ID_PATTERN = Pattern.compile(
        "^[A-Za-z0-9]+$"
    );
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidId(String id) {
        return id != null && ID_PATTERN.matcher(id).matches();
    }
    
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isValidGrade(String grade) {
        if (grade == null) return false;
        String[] validGrades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        for (String valid : validGrades) {
            if (valid.equalsIgnoreCase(grade)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidScore(double score) {
        return score >= 0 && score <= 100;
    }
    
    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 6;
    }
    
    public static boolean isValidCGPA(double cgpa) {
        return cgpa >= 0 && cgpa <= 4.0;
    }
    
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"']", "");
    }
}
