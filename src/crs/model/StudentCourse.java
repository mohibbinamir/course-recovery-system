package crs.model;

import java.io.Serializable;

public class StudentCourse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private Course course;
    private String semester;
    private int year;
    private String grade;
    private double examScore;
    private double assignmentScore;
    private int attemptNumber;
    private String status;
    
    public StudentCourse() {
        this.attemptNumber = 1;
        this.status = "In Progress";
    }
    
    public StudentCourse(String studentId, Course course, String semester, int year) {
        this();
        this.studentId = studentId;
        this.course = course;
        this.semester = semester;
        this.year = year;
    }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { 
        this.grade = grade;
        updateStatus();
    }
    
    public double getExamScore() { return examScore; }
    public void setExamScore(double examScore) { this.examScore = examScore; }
    
    public double getAssignmentScore() { return assignmentScore; }
    public void setAssignmentScore(double assignmentScore) { this.assignmentScore = assignmentScore; }
    
    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isFailed() {
        return grade != null && grade.equalsIgnoreCase("F");
    }
    
    public boolean isPassed() {
        if (grade == null) return false;
        return !grade.equalsIgnoreCase("F");
    }
    
    public double getGradePoint() {
        if (grade == null) return 0.0;
        switch (grade.toUpperCase()) {
            case "A": case "A+": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
    
    public boolean isExamFailed() {
        return examScore < 50;
    }
    
    public boolean isAssignmentFailed() {
        return assignmentScore < 50;
    }
    
    public String getFailedComponent() {
        if (isExamFailed() && isAssignmentFailed()) {
            return "Exam & Assignment";
        } else if (isExamFailed()) {
            return "Exam";
        } else if (isAssignmentFailed()) {
            return "Assignment";
        }
        return "None";
    }
    
    private void updateStatus() {
        if (grade == null) {
            status = "In Progress";
        } else if (isFailed()) {
            status = "Failed";
        } else {
            status = "Passed";
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (Grade: %s)", 
            course.getCourseId(), course.getCourseName(), grade != null ? grade : "N/A");
    }
}
