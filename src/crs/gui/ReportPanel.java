package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ReportPanel extends JPanel {
    
    private MainFrame mainFrame;
    private ReportService reportService;
    private PDFService pdfService;
    private StudentService studentService;
    private RecoveryPlanService recoveryPlanService;
    private EmailService emailService;
    
    private JTextArea reportArea;
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> studentCombo;
    private JComboBox<String> semesterCombo;
    private JTextField yearField;
    
    public ReportPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.reportService = ReportService.getInstance();
        this.pdfService = PDFService.getInstance();
        this.studentService = StudentService.getInstance();
        this.recoveryPlanService = RecoveryPlanService.getInstance();
        this.emailService = EmailService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel optionsPanel = createOptionsPanel();
        mainPanel.add(optionsPanel, BorderLayout.WEST);
        
        JPanel reportPanel = createReportPanel();
        mainPanel.add(reportPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("Academic Performance Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(280, 0));
        
        JLabel optionsTitle = new JLabel("Report Options");
        optionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        optionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(optionsTitle);
        panel.add(Box.createVerticalStrut(20));
        
        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(typeLabel);
        panel.add(Box.createVerticalStrut(5));
        
        String[] reportTypes = {
            "Student Academic Report",
            "Full Academic Transcript",
            "Eligibility Status Report",
            "Recovery Plan Report"
        };
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        reportTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportTypeCombo.addActionListener(e -> updateOptionsVisibility());
        panel.add(reportTypeCombo);
        panel.add(Box.createVerticalStrut(15));
        
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(studentLabel);
        panel.add(Box.createVerticalStrut(5));
        
        studentCombo = new JComboBox<>();
        studentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        studentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(studentCombo);
        panel.add(Box.createVerticalStrut(15));
        
        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        semesterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(semesterLabel);
        panel.add(Box.createVerticalStrut(5));
        
        String[] semesters = {"Fall", "Spring", "Summer"};
        semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        semesterCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(semesterCombo);
        panel.add(Box.createVerticalStrut(15));
        
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(yearLabel);
        panel.add(Box.createVerticalStrut(5));
        
        yearField = new JTextField("2024");
        yearField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        yearField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(yearField);
        panel.add(Box.createVerticalStrut(25));
        
        JButton generateButton = createButton("Generate Report", new Color(52, 152, 219));
        generateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateButton.addActionListener(e -> generateReport());
        panel.add(generateButton);
        panel.add(Box.createVerticalStrut(10));
        
        JButton exportPdfButton = createButton("Export to PDF", new Color(46, 204, 113));
        exportPdfButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        exportPdfButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportPdfButton.addActionListener(e -> exportToPDF());
        panel.add(exportPdfButton);
        panel.add(Box.createVerticalStrut(10));
        
        JButton emailButton = createButton("Send via Email", new Color(155, 89, 182));
        emailButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailButton.addActionListener(e -> sendReportByEmail());
        panel.add(emailButton);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        JLabel previewLabel = new JLabel("  Report Preview");
        previewLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        previewLabel.setPreferredSize(new Dimension(0, 40));
        previewLabel.setBackground(new Color(245, 247, 250));
        previewLabel.setOpaque(true);
        panel.add(previewLabel, BorderLayout.NORTH);
        
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        reportArea.setText("Select report options and click 'Generate Report' to preview.");
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refresh() {
        studentCombo.removeAllItems();
        List<Student> students = studentService.getAllStudents();
        for (Student student : students) {
            studentCombo.addItem(student.getStudentId() + " - " + student.getFullName());
        }
    }
    
    private void updateOptionsVisibility() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        boolean showStudent = !reportType.equals("Eligibility Status Report");
        boolean showSemester = reportType.equals("Student Academic Report");
        
        studentCombo.setEnabled(showStudent);
        semesterCombo.setEnabled(showSemester);
        yearField.setEnabled(showSemester);
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String report = "";
        
        try {
            switch (reportType) {
                case "Student Academic Report":
                    if (studentCombo.getSelectedIndex() < 0) {
                        JOptionPane.showMessageDialog(this, "Please select a student.");
                        return;
                    }
                    String studentEntry = (String) studentCombo.getSelectedItem();
                    String studentId = studentEntry.split(" - ")[0];
                    String semester = (String) semesterCombo.getSelectedItem();
                    int year = Integer.parseInt(yearField.getText().trim());
                    report = reportService.generateAcademicReport(studentId, semester, year);
                    break;
                    
                case "Full Academic Transcript":
                    if (studentCombo.getSelectedIndex() < 0) {
                        JOptionPane.showMessageDialog(this, "Please select a student.");
                        return;
                    }
                    studentEntry = (String) studentCombo.getSelectedItem();
                    studentId = studentEntry.split(" - ")[0];
                    report = reportService.generateFullAcademicReport(studentId);
                    break;
                    
                case "Eligibility Status Report":
                    report = reportService.generateEligibilityReport();
                    break;
                    
                case "Recovery Plan Report":
                    if (studentCombo.getSelectedIndex() < 0) {
                        JOptionPane.showMessageDialog(this, "Please select a student.");
                        return;
                    }
                    studentEntry = (String) studentCombo.getSelectedItem();
                    studentId = studentEntry.split(" - ")[0];
                    List<RecoveryPlan> plans = recoveryPlanService.getRecoveryPlansForStudent(studentId);
                    if (plans.isEmpty()) {
                        report = "No recovery plans found for this student.";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (RecoveryPlan plan : plans) {
                            sb.append(reportService.generateRecoveryPlanReport(plan));
                            sb.append("\n\n");
                        }
                        report = sb.toString();
                    }
                    break;
            }
            
            reportArea.setText(report);
            reportArea.setCaretPosition(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportToPDF() {
        if (reportArea.getText().isEmpty() || 
            reportArea.getText().startsWith("Select report")) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setSelectedFile(new File("report.pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String outputPath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!outputPath.endsWith(".pdf")) {
                outputPath += ".pdf";
            }
            
            String reportType = (String) reportTypeCombo.getSelectedItem();
            boolean success = false;
            
            try {
                switch (reportType) {
                    case "Student Academic Report":
                    case "Full Academic Transcript":
                        String studentEntry = (String) studentCombo.getSelectedItem();
                        String studentId = studentEntry.split(" - ")[0];
                        success = pdfService.exportAcademicReportToPDF(studentId, outputPath);
                        break;
                        
                    case "Eligibility Status Report":
                        success = pdfService.exportEligibilityReportToPDF(outputPath);
                        break;
                        
                    case "Recovery Plan Report":
                        studentEntry = (String) studentCombo.getSelectedItem();
                        studentId = studentEntry.split(" - ")[0];
                        List<RecoveryPlan> plans = recoveryPlanService.getRecoveryPlansForStudent(studentId);
                        if (!plans.isEmpty()) {
                            success = pdfService.exportRecoveryPlanToPDF(plans.get(0), outputPath);
                        }
                        break;
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "PDF exported successfully to:\n" + outputPath);
                } else {
                    reportService.saveReportToFile(reportArea.getText(), 
                        outputPath.replace(".pdf", ".txt"));
                    JOptionPane.showMessageDialog(this, 
                        "PDF library not available. Report saved as text file.");
                }
            } catch (Exception e) {
                reportService.saveReportToFile(reportArea.getText(), 
                    outputPath.replace(".pdf", ".txt"));
                JOptionPane.showMessageDialog(this, 
                    "Report saved as text file (PDF export requires iText library).");
            }
        }
    }
    
    private void sendReportByEmail() {
        if (reportArea.getText().isEmpty() || 
            reportArea.getText().startsWith("Select report")) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.");
            return;
        }
        
        String reportType = (String) reportTypeCombo.getSelectedItem();
        
        if (reportType.equals("Eligibility Status Report")) {
            JOptionPane.showMessageDialog(this, 
                "Eligibility report cannot be sent to individual students.");
            return;
        }
        
        String studentEntry = (String) studentCombo.getSelectedItem();
        String studentId = studentEntry.split(" - ")[0];
        Student student = studentService.findById(studentId);
        
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Send report to " + student.getEmail() + "?",
            "Confirm Email", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sent = emailService.sendAcademicReportEmail(student, reportArea.getText());
            if (sent) {
                JOptionPane.showMessageDialog(this, "Report sent successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to send email.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
}
