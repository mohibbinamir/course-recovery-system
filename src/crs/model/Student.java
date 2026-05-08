package crs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private String year;
    private String program;
    private boolean isEnrolled;
    private List<StudentCourse> courses;
    
    public Student() {
        this.courses = new ArrayList<>();
        this.isEnrolled = true;
    }
    
    public Student(String studentId, String firstName, String lastName, String email, String major, String year) {
        this();
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.year = year;
        this.program = "Bachelor of " + major;
    }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    
    public boolean isEnrolled() { return isEnrolled; }
    public void setEnrolled(boolean enrolled) { isEnrolled = enrolled; }
    
    public List<StudentCourse> getCourses() { return courses; }
    public void setCourses(List<StudentCourse> courses) { this.courses = courses; }
    
    public void addCourse(StudentCourse course) {
        this.courses.add(course);
    }
    
    public double calculateCGPA() {
        if (courses.isEmpty()) return 0.0;
        
        double totalGradePoints = 0;
        int totalCredits = 0;
        
        for (StudentCourse sc : courses) {
            if (sc.getGrade() != null && !sc.getGrade().isEmpty()) {
                double gradePoint = getGradePoint(sc.getGrade());
                int credits = sc.getCourse().getCredits();
                totalGradePoints += gradePoint * credits;
                totalCredits += credits;
            }
        }
        
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }
    
    public int getFailedCoursesCount() {
        int count = 0;
        for (StudentCourse sc : courses) {
            if (sc.isFailed()) {
                count++;
            }
        }
        return count;
    }
    
    public List<StudentCourse> getFailedCourses() {
        List<StudentCourse> failed = new ArrayList<>();
        for (StudentCourse sc : courses) {
            if (sc.isFailed()) {
                failed.add(sc);
            }
        }
        return failed;
    }
    
    public boolean isEligibleToProgress() {
        return calculateCGPA() >= 2.0 && getFailedCoursesCount() <= 3;
    }
    
    private double getGradePoint(String grade) {
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
    
    @Override
    public String toString() {
        return String.format("%s - %s %s (%s)", studentId, firstName, lastName, major);
    }
}
