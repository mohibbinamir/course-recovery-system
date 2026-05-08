package crs.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Milestone implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String milestoneId;
    private String planId;
    private String studyWeek;
    private String task;
    private String description;
    private LocalDate dueDate;
    private String status;
    private double grade;
    private String feedback;
    private LocalDate completedDate;
    
    public Milestone() {
        this.status = "Pending";
    }
    
    public Milestone(String milestoneId, String planId, String studyWeek, String task) {
        this();
        this.milestoneId = milestoneId;
        this.planId = planId;
        this.studyWeek = studyWeek;
        this.task = task;
    }
    
    public String getMilestoneId() { return milestoneId; }
    public void setMilestoneId(String milestoneId) { this.milestoneId = milestoneId; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getStudyWeek() { return studyWeek; }
    public void setStudyWeek(String studyWeek) { this.studyWeek = studyWeek; }
    
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getGrade() { return grade; }
    public void setGrade(double grade) { this.grade = grade; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
    
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate) && !"Completed".equals(status);
    }
    
    public void markCompleted(double grade, String feedback) {
        this.status = "Completed";
        this.grade = grade;
        this.feedback = feedback;
        this.completedDate = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s (%s)", studyWeek, task, status);
    }
}
