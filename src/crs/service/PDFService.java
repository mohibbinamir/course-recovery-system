package crs.service;

import crs.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFService {
    
    private static PDFService instance;
    private StudentService studentService;
    private CourseService courseService;
    
    private PDFService() {
        studentService = StudentService.getInstance();
        courseService = CourseService.getInstance();
    }
    
    public static PDFService getInstance() {
        if (instance == null) {
            instance = new PDFService();
        }
        return instance;
    }
    
    public boolean exportAcademicReportToPDF(String studentId, String outputPath) {
        Student student = studentService.findById(studentId);
        if (student == null) return false;
        
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(
                "ACADEMIC PERFORMANCE REPORT", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            document.add(new com.itextpdf.text.Paragraph("Student Name: " + student.getFullName(), headerFont));
            document.add(new com.itextpdf.text.Paragraph("Student ID: " + student.getStudentId(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Program: " + student.getProgram(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Major: " + student.getMajor(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Email: " + student.getEmail(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Generated: " + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), normalFont));
            document.add(new com.itextpdf.text.Paragraph("\n"));
            
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{15, 35, 15, 15, 20});
            
            String[] headers = {"Code", "Course Title", "Credits", "Grade", "Grade Points"};
            for (String header : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                    new com.itextpdf.text.Phrase(header, headerFont));
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            List<StudentCourse> courses = studentService.getStudentCourses(studentId);
            double totalPoints = 0;
            int totalCredits = 0;
            
            for (StudentCourse sc : courses) {
                Course course = sc.getCourse();
                String grade = sc.getGrade() != null ? sc.getGrade() : "N/A";
                double gradePoint = sc.getGradePoint();
                
                table.addCell(new com.itextpdf.text.Phrase(course.getCourseId(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(course.getCourseName(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(String.valueOf(course.getCredits()), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(grade, normalFont));
                table.addCell(new com.itextpdf.text.Phrase(String.format("%.1f", gradePoint), normalFont));
                
                if (sc.getGrade() != null) {
                    totalPoints += gradePoint * course.getCredits();
                    totalCredits += course.getCredits();
                }
            }
            
            document.add(table);
            document.add(new com.itextpdf.text.Paragraph("\n"));
            
            double cgpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
            document.add(new com.itextpdf.text.Paragraph(
                String.format("Cumulative GPA (CGPA): %.2f", cgpa), headerFont));
            
            String eligibility = student.isEligibleToProgress() ? "ELIGIBLE" : "NOT ELIGIBLE";
            document.add(new com.itextpdf.text.Paragraph(
                "Eligibility Status: " + eligibility, headerFont));
            
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            return false;
        }
    }
    
    public boolean exportRecoveryPlanToPDF(RecoveryPlan plan, String outputPath) {
        Student student = studentService.findById(plan.getStudentId());
        Course course = courseService.findById(plan.getCourseId());
        
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(
                "COURSE RECOVERY PLAN", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            document.add(new com.itextpdf.text.Paragraph("Plan ID: " + plan.getPlanId(), headerFont));
            document.add(new com.itextpdf.text.Paragraph("Status: " + plan.getStatus(), normalFont));
            document.add(new com.itextpdf.text.Paragraph(
                String.format("Progress: %.1f%%", plan.getProgressPercentage()), normalFont));
            document.add(new com.itextpdf.text.Paragraph("\n"));
            
            if (student != null) {
                document.add(new com.itextpdf.text.Paragraph("Student Information", headerFont));
                document.add(new com.itextpdf.text.Paragraph("Name: " + student.getFullName(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("ID: " + student.getStudentId(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("Email: " + student.getEmail(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("\n"));
            }
            
            if (course != null) {
                document.add(new com.itextpdf.text.Paragraph("Course Information", headerFont));
                document.add(new com.itextpdf.text.Paragraph("Course: " + course.getCourseName(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("Code: " + course.getCourseId(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("Instructor: " + course.getInstructor(), normalFont));
                document.add(new com.itextpdf.text.Paragraph("\n"));
            }
            
            document.add(new com.itextpdf.text.Paragraph("Plan Details", headerFont));
            document.add(new com.itextpdf.text.Paragraph("Start Date: " + plan.getStartDate(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("End Date: " + plan.getEndDate(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Recommendation: " + plan.getRecommendation(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("\n"));
            
            document.add(new com.itextpdf.text.Paragraph("Milestones", headerFont));
            
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{20, 40, 20, 20});
            
            String[] headers = {"Study Week", "Task", "Status", "Grade"};
            for (String header : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                    new com.itextpdf.text.Phrase(header, headerFont));
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            for (Milestone m : plan.getMilestones()) {
                table.addCell(new com.itextpdf.text.Phrase(m.getStudyWeek(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(m.getTask(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(m.getStatus(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(
                    "Completed".equals(m.getStatus()) ? String.format("%.1f", m.getGrade()) : "-", normalFont));
            }
            
            document.add(table);
            document.add(new com.itextpdf.text.Paragraph("\n"));
            document.add(new com.itextpdf.text.Paragraph("Created by: " + plan.getCreatedBy(), normalFont));
            document.add(new com.itextpdf.text.Paragraph("Created on: " + plan.getCreatedAt(), normalFont));
            
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            return false;
        }
    }
    
    public boolean exportEligibilityReportToPDF(String outputPath) {
        List<Student> ineligible = studentService.getIneligibleStudents();
        
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
            
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(
                "ELIGIBILITY STATUS REPORT", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            document.add(new com.itextpdf.text.Paragraph("Generated: " + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), normalFont));
            document.add(new com.itextpdf.text.Paragraph("\n"));
            document.add(new com.itextpdf.text.Paragraph("Students NOT Eligible to Progress:", headerFont));
            document.add(new com.itextpdf.text.Paragraph("\n"));
            
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{15, 25, 20, 15, 25});
            
            String[] headers = {"ID", "Name", "Major", "CGPA", "Reason"};
            for (String header : headers) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                    new com.itextpdf.text.Phrase(header, headerFont));
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }
            
            for (Student student : ineligible) {
                double cgpa = studentService.calculateCGPA(student.getStudentId());
                int failed = student.getFailedCoursesCount();
                String reason = "";
                if (cgpa < 2.0) reason = "Low CGPA";
                if (failed > 3) reason += (reason.isEmpty() ? "" : ", ") + ">3 Failed";
                
                table.addCell(new com.itextpdf.text.Phrase(student.getStudentId(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(student.getFullName(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(student.getMajor(), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(String.format("%.2f", cgpa), normalFont));
                table.addCell(new com.itextpdf.text.Phrase(reason, normalFont));
            }
            
            document.add(table);
            document.add(new com.itextpdf.text.Paragraph("\n"));
            document.add(new com.itextpdf.text.Paragraph(
                "Total Ineligible Students: " + ineligible.size(), headerFont));
            
            document.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating PDF: " + e.getMessage());
            return false;
        }
    }
}
