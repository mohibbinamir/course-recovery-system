package crs.model;

import java.io.Serializable;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String courseId;
    private String courseName;
    private int credits;
    private String semester;
    private String instructor;
    private int examWeight;
    private int assignmentWeight;
    private String description;
    
    public Course() {}
    
    public Course(String courseId, String courseName, int credits, String semester, 
                  String instructor, int examWeight, int assignmentWeight) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.instructor = instructor;
        this.examWeight = examWeight;
        this.assignmentWeight = assignmentWeight;
    }
    
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    
    public int getExamWeight() { return examWeight; }
    public void setExamWeight(int examWeight) { this.examWeight = examWeight; }
    
    public int getAssignmentWeight() { return assignmentWeight; }
    public void setAssignmentWeight(int assignmentWeight) { this.assignmentWeight = assignmentWeight; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%d credits)", courseId, courseName, credits);
    }
}
