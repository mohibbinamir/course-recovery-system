package crs.service;

import crs.model.*;
import crs.util.FileManager;

import java.time.LocalDate;
import java.util.*;

public class RecoveryPlanService {
    
    private static final String RECOVERY_PLANS_FILE = "recovery_plans.dat";
    
    private List<RecoveryPlan> recoveryPlans;
    
    private static RecoveryPlanService instance;
    
    private RecoveryPlanService() {
        loadRecoveryPlans();
    }
    
    public static RecoveryPlanService getInstance() {
        if (instance == null) {
            instance = new RecoveryPlanService();
        }
        return instance;
    }
    
    private void loadRecoveryPlans() {
        recoveryPlans = FileManager.loadFromBinaryFile(RECOVERY_PLANS_FILE);
        if (recoveryPlans == null) {
            recoveryPlans = new ArrayList<>();
        }
    }
    
    public void saveRecoveryPlans() {
        FileManager.saveToBinaryFile(RECOVERY_PLANS_FILE, recoveryPlans);
    }
    
    public RecoveryPlan createRecoveryPlan(String studentId, String courseId, String recommendation,
                                           LocalDate startDate, LocalDate endDate, String createdBy) {
        String planId = generatePlanId();
        
        RecoveryPlan plan = new RecoveryPlan(planId, studentId, courseId);
        plan.setRecommendation(recommendation);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setCreatedBy(createdBy);
        plan.setStatus("Active");
        
        recoveryPlans.add(plan);
        saveRecoveryPlans();
        return plan;
    }
    
    public void updateRecoveryPlan(RecoveryPlan plan) {
        for (int i = 0; i < recoveryPlans.size(); i++) {
            if (recoveryPlans.get(i).getPlanId().equals(plan.getPlanId())) {
                recoveryPlans.set(i, plan);
                saveRecoveryPlans();
                return;
            }
        }
    }
    
    public void deleteRecoveryPlan(String planId) {
        recoveryPlans.removeIf(p -> p.getPlanId().equals(planId));
        saveRecoveryPlans();
    }
    
    public RecoveryPlan findById(String planId) {
        for (RecoveryPlan plan : recoveryPlans) {
            if (plan.getPlanId().equals(planId)) {
                return plan;
            }
        }
        return null;
    }
    
    public List<RecoveryPlan> getAllRecoveryPlans() {
        return new ArrayList<>(recoveryPlans);
    }
    
    public List<RecoveryPlan> getRecoveryPlansForStudent(String studentId) {
        List<RecoveryPlan> plans = new ArrayList<>();
        for (RecoveryPlan plan : recoveryPlans) {
            if (plan.getStudentId().equals(studentId)) {
                plans.add(plan);
            }
        }
        return plans;
    }
    
    public List<RecoveryPlan> getRecoveryPlansForCourse(String courseId) {
        List<RecoveryPlan> plans = new ArrayList<>();
        for (RecoveryPlan plan : recoveryPlans) {
            if (plan.getCourseId().equals(courseId)) {
                plans.add(plan);
            }
        }
        return plans;
    }
    
    public List<RecoveryPlan> getActivePlans() {
        List<RecoveryPlan> active = new ArrayList<>();
        for (RecoveryPlan plan : recoveryPlans) {
            if ("Active".equals(plan.getStatus())) {
                active.add(plan);
            }
        }
        return active;
    }
    
    public Milestone addMilestone(String planId, String studyWeek, String task, 
                                   String description, LocalDate dueDate) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            String milestoneId = generateMilestoneId();
            Milestone milestone = new Milestone(milestoneId, planId, studyWeek, task);
            milestone.setDescription(description);
            milestone.setDueDate(dueDate);
            
            plan.addMilestone(milestone);
            saveRecoveryPlans();
            return milestone;
        }
        return null;
    }
    
    public void updateMilestone(String planId, Milestone milestone) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            List<Milestone> milestones = plan.getMilestones();
            for (int i = 0; i < milestones.size(); i++) {
                if (milestones.get(i).getMilestoneId().equals(milestone.getMilestoneId())) {
                    milestones.set(i, milestone);
                    saveRecoveryPlans();
                    return;
                }
            }
        }
    }
    
    public void removeMilestone(String planId, String milestoneId) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            plan.getMilestones().removeIf(m -> m.getMilestoneId().equals(milestoneId));
            saveRecoveryPlans();
        }
    }
    
    public void gradeMilestone(String planId, String milestoneId, double grade, String feedback) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            for (Milestone milestone : plan.getMilestones()) {
                if (milestone.getMilestoneId().equals(milestoneId)) {
                    milestone.markCompleted(grade, feedback);
                    
                    if (plan.isCompleted()) {
                        plan.setStatus("Completed");
                    }
                    
                    saveRecoveryPlans();
                    return;
                }
            }
        }
    }
    
    public void updateProgress(String planId, String milestoneId, String status) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            for (Milestone milestone : plan.getMilestones()) {
                if (milestone.getMilestoneId().equals(milestoneId)) {
                    milestone.setStatus(status);
                    saveRecoveryPlans();
                    return;
                }
            }
        }
    }
    
    public double getPlanProgress(String planId) {
        RecoveryPlan plan = findById(planId);
        return plan != null ? plan.getProgressPercentage() : 0.0;
    }
    
    public void completePlan(String planId) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            plan.setStatus("Completed");
            saveRecoveryPlans();
        }
    }
    
    public void cancelPlan(String planId) {
        RecoveryPlan plan = findById(planId);
        if (plan != null) {
            plan.setStatus("Cancelled");
            saveRecoveryPlans();
        }
    }
    
    private String generatePlanId() {
        return "RP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateMilestoneId() {
        return "MS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
