package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class EligibilityPanel extends JPanel {
    
    private MainFrame mainFrame;
    private StudentService studentService;
    private EmailService emailService;
    
    private JTable eligibilityTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    
    public EligibilityPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.studentService = StudentService.getInstance();
        this.emailService = EmailService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("Eligibility Check & Enrolment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        String[] filters = {"All Students", "Eligible Only", "Not Eligible Only"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setPreferredSize(new Dimension(150, 35));
        filterCombo.addActionListener(e -> filterTable());
        actionsPanel.add(filterCombo);
        
        JButton notifyButton = createButton("Notify All", new Color(155, 89, 182));
        notifyButton.addActionListener(e -> notifyIneligibleStudents());
        actionsPanel.add(notifyButton);
        
        JButton refreshButton = createButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> refresh());
        actionsPanel.add(refreshButton);
        
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        String[] columns = {"Student ID", "Name", "Major", "CGPA", "Failed Courses", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        eligibilityTable = new JTable(tableModel);
        eligibilityTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eligibilityTable.setRowHeight(40);
        eligibilityTable.setShowGrid(false);
        eligibilityTable.setIntercellSpacing(new Dimension(0, 0));
        eligibilityTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        eligibilityTable.getTableHeader().setBackground(new Color(245, 247, 250));
        eligibilityTable.getTableHeader().setForeground(new Color(100, 100, 100));
        eligibilityTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        eligibilityTable.getColumn("Action").setCellRenderer(new EligibilityButtonRenderer());
        eligibilityTable.getColumn("Action").setCellEditor(new EligibilityButtonEditor(new JCheckBox()));
        
        eligibilityTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 5);
                    if ("Not Eligible".equals(status)) {
                        c.setBackground(new Color(255, 235, 235));
                    } else {
                        c.setBackground(new Color(235, 255, 235));
                    }
                }
                
                if (column == 5) {
                    String status = (String) value;
                    if ("Not Eligible".equals(status)) {
                        setForeground(new Color(192, 57, 43));
                    } else {
                        setForeground(new Color(39, 174, 96));
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(eligibilityTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));
        
        return panel;
    }
    
    private void updateSummary() {
        JPanel summaryPanel = (JPanel) getComponent(2);
        summaryPanel.removeAll();
        
        List<Student> allStudents = studentService.getAllStudents();
        int total = allStudents.size();
        int eligible = 0;
        int notEligible = 0;
        
        for (Student student : allStudents) {
            if (studentService.isEligibleToProgress(student.getStudentId())) {
                eligible++;
            } else {
                notEligible++;
            }
        }
        
        JLabel totalLabel = createSummaryLabel("Total Students: " + total, new Color(52, 152, 219));
        JLabel eligibleLabel = createSummaryLabel("Eligible: " + eligible, new Color(46, 204, 113));
        JLabel notEligibleLabel = createSummaryLabel("Not Eligible: " + notEligible, new Color(231, 76, 60));
        
        summaryPanel.add(totalLabel);
        summaryPanel.add(eligibleLabel);
        summaryPanel.add(notEligibleLabel);
        
        summaryPanel.add(Box.createHorizontalStrut(30));
        
        JLabel criteriaLabel = new JLabel("Eligibility Criteria: CGPA >= 2.0 AND Failed Courses <= 3");
        criteriaLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        criteriaLabel.setForeground(new Color(120, 120, 120));
        summaryPanel.add(criteriaLabel);
        
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }
    
    private JLabel createSummaryLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        return label;
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        
        for (Student student : students) {
            double cgpa = studentService.calculateCGPA(student.getStudentId());
            
            studentService.reloadData();
            Student reloaded = studentService.findById(student.getStudentId());
            int failedCount = reloaded != null ? reloaded.getFailedCoursesCount() : 0;
            
            boolean eligible = cgpa >= 2.0 && failedCount <= 3;
            String status = eligible ? "Eligible" : "Not Eligible";
            
            Object[] row = {
                student.getStudentId(),
                student.getFullName(),
                student.getMajor(),
                String.format("%.2f", cgpa),
                failedCount,
                status,
                "Action"
            };
            tableModel.addRow(row);
        }
        
        updateSummary();
    }
    
    private void filterTable() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        
        for (Student student : students) {
            double cgpa = studentService.calculateCGPA(student.getStudentId());
            
            studentService.reloadData();
            Student reloaded = studentService.findById(student.getStudentId());
            int failedCount = reloaded != null ? reloaded.getFailedCoursesCount() : 0;
            
            boolean eligible = cgpa >= 2.0 && failedCount <= 3;
            String status = eligible ? "Eligible" : "Not Eligible";
            
            boolean include = "All Students".equals(filter) ||
                ("Eligible Only".equals(filter) && eligible) ||
                ("Not Eligible Only".equals(filter) && !eligible);
            
            if (include) {
                Object[] row = {
                    student.getStudentId(),
                    student.getFullName(),
                    student.getMajor(),
                    String.format("%.2f", cgpa),
                    failedCount,
                    status,
                    "Action"
                };
                tableModel.addRow(row);
            }
        }
        
        updateSummary();
    }
    
    private void notifyIneligibleStudents() {
        List<Student> ineligible = studentService.getIneligibleStudents();
        
        if (ineligible.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ineligible students to notify.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Send notification emails to " + ineligible.size() + " ineligible students?",
            "Confirm Notification", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int sent = 0;
            for (Student student : ineligible) {
                double cgpa = studentService.calculateCGPA(student.getStudentId());
                if (emailService.sendEligibilityNotificationEmail(student, false, cgpa)) {
                    sent++;
                }
            }
            JOptionPane.showMessageDialog(this,
                "Sent " + sent + " notification emails successfully.");
        }
    }
    
    private void confirmEnrolment(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) return;
        
        double cgpa = studentService.calculateCGPA(studentId);
        boolean eligible = studentService.isEligibleToProgress(studentId);
        
        if (!eligible) {
            JOptionPane.showMessageDialog(this,
                "Student is not eligible for enrolment.\nCGPA: " + String.format("%.2f", cgpa),
                "Cannot Enroll", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm enrolment for " + student.getFullName() + "?\nCGPA: " + String.format("%.2f", cgpa),
            "Confirm Enrolment", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            studentService.confirmEnrolment(studentId);
            emailService.sendEligibilityNotificationEmail(student, true, cgpa);
            JOptionPane.showMessageDialog(this,
                "Student enrolled successfully!\nConfirmation email sent.");
            refresh();
        }
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    class EligibilityButtonRenderer extends JPanel implements TableCellRenderer {
        public EligibilityButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            
            String status = (String) table.getValueAt(row, 5);
            setBackground(isSelected ? table.getSelectionBackground() : 
                ("Eligible".equals(status) ? new Color(235, 255, 235) : new Color(255, 235, 235)));
            
            if ("Eligible".equals(status)) {
                JButton enrollBtn = new JButton("Enroll");
                enrollBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                enrollBtn.setPreferredSize(new Dimension(60, 24));
                enrollBtn.setBackground(new Color(46, 204, 113));
                enrollBtn.setForeground(Color.WHITE);
                add(enrollBtn);
            } else {
                JButton viewBtn = new JButton("Details");
                viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                viewBtn.setPreferredSize(new Dimension(60, 24));
                add(viewBtn);
            }
            
            return this;
        }
    }
    
    class EligibilityButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private String studentId;
        private String status;
        
        public EligibilityButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            panel.removeAll();
            
            studentId = (String) table.getValueAt(row, 0);
            status = (String) table.getValueAt(row, 5);
            
            if ("Eligible".equals(status)) {
                JButton enrollBtn = new JButton("Enroll");
                enrollBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                enrollBtn.setPreferredSize(new Dimension(60, 24));
                enrollBtn.setBackground(new Color(46, 204, 113));
                enrollBtn.setForeground(Color.WHITE);
                enrollBtn.addActionListener(e -> {
                    fireEditingStopped();
                    confirmEnrolment(studentId);
                });
                panel.add(enrollBtn);
            } else {
                JButton viewBtn = new JButton("Details");
                viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                viewBtn.setPreferredSize(new Dimension(60, 24));
                viewBtn.addActionListener(e -> {
                    fireEditingStopped();
                    showIneligibilityDetails(studentId);
                });
                panel.add(viewBtn);
            }
            
            panel.setBackground(new Color(245, 245, 245));
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Action";
        }
    }
    
    private void showIneligibilityDetails(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) return;
        
        double cgpa = studentService.calculateCGPA(studentId);
        studentService.reloadData();
        Student reloaded = studentService.findById(studentId);
        int failedCount = reloaded != null ? reloaded.getFailedCoursesCount() : 0;
        
        StringBuilder message = new StringBuilder();
        message.append("Student: ").append(student.getFullName()).append("\n\n");
        message.append("CGPA: ").append(String.format("%.2f", cgpa));
        message.append(cgpa < 2.0 ? " (Below minimum 2.0)" : " (OK)").append("\n");
        message.append("Failed Courses: ").append(failedCount);
        message.append(failedCount > 3 ? " (Exceeds maximum 3)" : " (OK)").append("\n\n");
        
        if (reloaded != null && !reloaded.getFailedCourses().isEmpty()) {
            message.append("Failed Courses:\n");
            for (StudentCourse sc : reloaded.getFailedCourses()) {
                message.append("  - ").append(sc.getCourse().getCourseName()).append("\n");
            }
        }
        
        message.append("\nRecommendation: Create a recovery plan for this student.");
        
        JOptionPane.showMessageDialog(this, message.toString(),
            "Ineligibility Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
