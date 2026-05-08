package crs.service;

import crs.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportService {
    
    private StudentService studentService;
    private CourseService courseService;
    
    private static ReportService instance;
    
    private ReportService() {
        studentService = StudentService.getInstance();
        courseService = CourseService.getInstance();
    }
    
    public static ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }
    
    public String generateAcademicReport(String studentId, String semester, int year) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            return "Student not found";
        }
        
        List<StudentCourse> courses = studentService.getStudentCoursesBySemester(studentId, semester, year);
        
        StringBuilder report = new StringBuilder();
        report.append("=" .repeat(60)).append("\n");
        report.append("           ACADEMIC PERFORMANCE REPORT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        report.append("Student Name: ").append(student.getFullName()).append("\n");
        report.append("Student ID: ").append(student.getStudentId()).append("\n");
        report.append("Program: ").append(student.getProgram()).append("\n");
        report.append("Semester: ").append(semester).append(" ").append(year).append("\n");
        report.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))).append("\n\n");
        
        report.append("-".repeat(60)).append("\n");
        report.append(String.format("%-10s %-25s %-8s %-6s %-8s%n", 
            "Code", "Course Title", "Credits", "Grade", "Points"));
        report.append("-".repeat(60)).append("\n");
        
        double totalGradePoints = 0;
        int totalCredits = 0;
        
        for (StudentCourse sc : courses) {
            Course course = sc.getCourse();
            String grade = sc.getGrade() != null ? sc.getGrade() : "N/A";
            double gradePoint = sc.getGradePoint();
            
            report.append(String.format("%-10s %-25s %-8d %-6s %-8.1f%n",
                course.getCourseId(),
                truncate(course.getCourseName(), 25),
                course.getCredits(),
                grade,
                gradePoint));
            
            if (sc.getGrade() != null) {
                totalGradePoints += gradePoint * course.getCredits();
                totalCredits += course.getCredits();
            }
        }
        
        report.append("-".repeat(60)).append("\n");
        
        double semesterGPA = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        double cgpa = studentService.calculateCGPA(studentId);
        
        report.append(String.format("%nSemester GPA: %.2f%n", semesterGPA));
        report.append(String.format("Cumulative GPA (CGPA): %.2f%n", cgpa));
        
        report.append("\n").append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
    
    public String generateFullAcademicReport(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) {
            return "Student not found";
        }
        
        List<StudentCourse> allCourses = studentService.getStudentCourses(studentId);
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("        COMPLETE ACADEMIC PERFORMANCE REPORT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        report.append("Student Name: ").append(student.getFullName()).append("\n");
        report.append("Student ID: ").append(student.getStudentId()).append("\n");
        report.append("Program: ").append(student.getProgram()).append("\n");
        report.append("Major: ").append(student.getMajor()).append("\n");
        report.append("Year: ").append(student.getYear()).append("\n");
        report.append("Email: ").append(student.getEmail()).append("\n");
        report.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))).append("\n\n");
        
        Map<String, List<StudentCourse>> bySemester = new LinkedHashMap<>();
        for (StudentCourse sc : allCourses) {
            String key = sc.getSemester() + " " + sc.getYear();
            bySemester.computeIfAbsent(key, k -> new ArrayList<>()).add(sc);
        }
        
        for (Map.Entry<String, List<StudentCourse>> entry : bySemester.entrySet()) {
            report.append("\n").append(entry.getKey()).append("\n");
            report.append("-".repeat(60)).append("\n");
            report.append(String.format("%-10s %-25s %-8s %-6s %-8s%n",
                "Code", "Course Title", "Credits", "Grade", "Points"));
            report.append("-".repeat(60)).append("\n");
            
            double semesterPoints = 0;
            int semesterCredits = 0;
            
            for (StudentCourse sc : entry.getValue()) {
                Course course = sc.getCourse();
                String grade = sc.getGrade() != null ? sc.getGrade() : "N/A";
                double gradePoint = sc.getGradePoint();
                
                report.append(String.format("%-10s %-25s %-8d %-6s %-8.1f%n",
                    course.getCourseId(),
                    truncate(course.getCourseName(), 25),
                    course.getCredits(),
                    grade,
                    gradePoint));
                
                if (sc.getGrade() != null) {
                    semesterPoints += gradePoint * course.getCredits();
                    semesterCredits += course.getCredits();
                }
            }
            
            double semesterGPA = semesterCredits > 0 ? semesterPoints / semesterCredits : 0.0;
            report.append(String.format("Semester GPA: %.2f%n", semesterGPA));
        }
        
        report.append("\n").append("=".repeat(60)).append("\n");
        report.append(String.format("CUMULATIVE GPA (CGPA): %.2f%n", studentService.calculateCGPA(studentId)));
        
        int failedCourses = student.getFailedCoursesCount();
        report.append("Failed Courses: ").append(failedCourses).append("\n");
        report.append("Eligibility Status: ").append(student.isEligibleToProgress() ? "ELIGIBLE" : "NOT ELIGIBLE").append("\n");
        report.append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
    
    public String generateEligibilityReport() {
        List<Student> ineligible = studentService.getIneligibleStudents();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(70)).append("\n");
        report.append("              ELIGIBILITY STATUS REPORT\n");
        report.append("=".repeat(70)).append("\n");
        report.append("Generated: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))).append("\n\n");
        
        report.append("Students NOT Eligible to Progress:\n");
        report.append("-".repeat(70)).append("\n");
        report.append(String.format("%-10s %-20s %-15s %-8s %-8s %-10s%n",
            "ID", "Name", "Major", "CGPA", "Failed", "Reason"));
        report.append("-".repeat(70)).append("\n");
        
        for (Student student : ineligible) {
            double cgpa = studentService.calculateCGPA(student.getStudentId());
            int failed = student.getFailedCoursesCount();
            String reason = "";
            if (cgpa < 2.0) reason = "Low CGPA";
            if (failed > 3) reason += (reason.isEmpty() ? "" : ", ") + ">3 Failed";
            
            report.append(String.format("%-10s %-20s %-15s %-8.2f %-8d %-10s%n",
                student.getStudentId(),
                truncate(student.getFullName(), 20),
                truncate(student.getMajor(), 15),
                cgpa,
                failed,
                reason));
        }
        
        report.append("-".repeat(70)).append("\n");
        report.append("Total Ineligible Students: ").append(ineligible.size()).append("\n");
        report.append("=".repeat(70)).append("\n");
        
        return report.toString();
    }
    
    public String generateRecoveryPlanReport(RecoveryPlan plan) {
        Student student = studentService.findById(plan.getStudentId());
        Course course = courseService.findById(plan.getCourseId());
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(60)).append("\n");
        report.append("           COURSE RECOVERY PLAN REPORT\n");
        report.append("=".repeat(60)).append("\n\n");
        
        report.append("Plan ID: ").append(plan.getPlanId()).append("\n");
        report.append("Status: ").append(plan.getStatus()).append("\n");
        report.append("Progress: ").append(String.format("%.1f%%", plan.getProgressPercentage())).append("\n\n");
        
        if (student != null) {
            report.append("Student: ").append(student.getFullName()).append(" (").append(student.getStudentId()).append(")\n");
            report.append("Email: ").append(student.getEmail()).append("\n");
        }
        
        if (course != null) {
            report.append("Course: ").append(course.getCourseName()).append(" (").append(course.getCourseId()).append(")\n");
            report.append("Instructor: ").append(course.getInstructor()).append("\n");
        }
        
        report.append("\nDuration: ").append(plan.getStartDate()).append(" to ").append(plan.getEndDate()).append("\n");
        report.append("Recommendation: ").append(plan.getRecommendation()).append("\n\n");
        
        report.append("Milestones:\n");
        report.append("-".repeat(60)).append("\n");
        report.append(String.format("%-12s %-25s %-10s %-8s%n", "Week", "Task", "Status", "Grade"));
        report.append("-".repeat(60)).append("\n");
        
        for (Milestone m : plan.getMilestones()) {
            report.append(String.format("%-12s %-25s %-10s %-8s%n",
                m.getStudyWeek(),
                truncate(m.getTask(), 25),
                m.getStatus(),
                "Completed".equals(m.getStatus()) ? String.format("%.1f", m.getGrade()) : "-"));
        }
        
        report.append("-".repeat(60)).append("\n");
        report.append("Created by: ").append(plan.getCreatedBy()).append("\n");
        report.append("Created on: ").append(plan.getCreatedAt()).append("\n");
        report.append("=".repeat(60)).append("\n");
        
        return report.toString();
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 2) + ".." : text;
    }
    
    public void saveReportToFile(String report, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/" + filename))) {
            writer.print(report);
        } catch (IOException e) {
            System.err.println("Error saving report: " + e.getMessage());
        }
    }
}
