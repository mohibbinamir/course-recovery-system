package crs.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecoveryPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String planId;
    private String studentId;
    private String courseId;
    private String recommendation;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private List<Milestone> milestones;
    private String createdBy;
    private LocalDate createdAt;
    private String notes;
    
    public RecoveryPlan() {
        this.milestones = new ArrayList<>();
        this.status = "Active";
        this.createdAt = LocalDate.now();
    }
    
    public RecoveryPlan(String planId, String studentId, String courseId) {
        this();
        this.planId = planId;
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public void addMilestone(Milestone milestone) {
        this.milestones.add(milestone);
    }
    
    public void removeMilestone(Milestone milestone) {
        this.milestones.remove(milestone);
    }
    
    public double getProgressPercentage() {
        if (milestones.isEmpty()) return 0.0;
        
        long completed = milestones.stream()
            .filter(m -> "Completed".equals(m.getStatus()))
            .count();
        
        return (completed * 100.0) / milestones.size();
    }
    
    public boolean isCompleted() {
        return milestones.stream().allMatch(m -> "Completed".equals(m.getStatus()));
    }
    
    @Override
    public String toString() {
        return String.format("Recovery Plan: %s - Student: %s, Course: %s", planId, studentId, courseId);
    }
}
