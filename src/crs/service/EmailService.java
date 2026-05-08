package crs.service;

import crs.model.*;
import crs.util.FileManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EmailService {
    
    private static final String EMAIL_LOG_FILE = "email_log.txt";
    
    private static EmailService instance;
    
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private boolean configured;
    
    private EmailService() {
        this.configured = false;
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    public void configure(String host, String port, String username, String password) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.smtpUsername = username;
        this.smtpPassword = password;
        this.configured = true;
    }
    
    public boolean isConfigured() {
        return configured;
    }
    
    public boolean sendEmail(String to, String subject, String body) {
        logEmail(to, subject, body, true);
        return true;
    }
    
    public boolean sendAccountCreatedEmail(User user, String tempPassword) {
        String subject = "Welcome to Course Recovery System - Account Created";
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getFullName()).append(",\n\n");
        body.append("Your account has been created in the Course Recovery System.\n\n");
        body.append("Username: ").append(user.getUsername()).append("\n");
        body.append("Temporary Password: ").append(tempPassword).append("\n\n");
        body.append("Please login and change your password immediately.\n\n");
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(user.getEmail(), subject, body.toString());
    }
    
    public boolean sendPasswordResetEmail(User user, String newPassword) {
        String subject = "Course Recovery System - Password Reset";
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(user.getFullName()).append(",\n\n");
        body.append("Your password has been reset.\n\n");
        body.append("New Password: ").append(newPassword).append("\n\n");
        body.append("Please login and change your password immediately.\n\n");
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(user.getEmail(), subject, body.toString());
    }
    
    public boolean sendRecoveryPlanEmail(Student student, RecoveryPlan plan, Course course) {
        String subject = "Course Recovery Plan Created - " + course.getCourseName();
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(student.getFullName()).append(",\n\n");
        body.append("A recovery plan has been created for you for the following course:\n\n");
        body.append("Course: ").append(course.getCourseName()).append(" (").append(course.getCourseId()).append(")\n");
        body.append("Start Date: ").append(plan.getStartDate()).append("\n");
        body.append("End Date: ").append(plan.getEndDate()).append("\n\n");
        body.append("Recommendation:\n").append(plan.getRecommendation()).append("\n\n");
        
        if (!plan.getMilestones().isEmpty()) {
            body.append("Milestones:\n");
            body.append("-".repeat(40)).append("\n");
            for (Milestone m : plan.getMilestones()) {
                body.append(m.getStudyWeek()).append(": ").append(m.getTask()).append("\n");
            }
            body.append("-".repeat(40)).append("\n");
        }
        
        body.append("\nPlease contact your academic advisor if you have any questions.\n\n");
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(student.getEmail(), subject, body.toString());
    }
    
    public boolean sendAcademicReportEmail(Student student, String reportContent) {
        String subject = "Academic Performance Report - " + student.getFullName();
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(student.getFullName()).append(",\n\n");
        body.append("Please find your academic performance report below:\n\n");
        body.append(reportContent);
        body.append("\nIf you have any questions, please contact the Academic Office.\n\n");
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(student.getEmail(), subject, body.toString());
    }
    
    public boolean sendMilestoneReminderEmail(Student student, RecoveryPlan plan, Milestone milestone) {
        String subject = "Milestone Reminder - " + milestone.getTask();
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(student.getFullName()).append(",\n\n");
        body.append("This is a reminder for your upcoming milestone:\n\n");
        body.append("Recovery Plan: ").append(plan.getPlanId()).append("\n");
        body.append("Milestone: ").append(milestone.getTask()).append("\n");
        body.append("Due Date: ").append(milestone.getDueDate()).append("\n\n");
        body.append("Please ensure you complete this task on time.\n\n");
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(student.getEmail(), subject, body.toString());
    }
    
    public boolean sendEligibilityNotificationEmail(Student student, boolean isEligible, double cgpa) {
        String subject = "Academic Eligibility Status Update";
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(student.getFullName()).append(",\n\n");
        
        if (isEligible) {
            body.append("Congratulations! You are eligible to progress to the next level of study.\n\n");
        } else {
            body.append("We regret to inform you that you are currently not eligible to progress.\n\n");
            body.append("Your current CGPA: ").append(String.format("%.2f", cgpa)).append("\n");
            body.append("Minimum required CGPA: 2.0\n\n");
            body.append("Please contact the Academic Office to discuss your options.\n\n");
        }
        
        body.append("Best regards,\n");
        body.append("Course Recovery System");
        
        return sendEmail(student.getEmail(), subject, body.toString());
    }
    
    private void logEmail(String to, String subject, String body, boolean success) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("[%s] To: %s | Subject: %s | Status: %s",
            timestamp, to, subject, success ? "SENT" : "FAILED");
        FileManager.appendToTextFile(EMAIL_LOG_FILE, logEntry);
    }
    
    public List<String> getEmailLogs() {
        return FileManager.readFromTextFile(EMAIL_LOG_FILE);
    }
}
