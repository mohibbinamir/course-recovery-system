package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecoveryPlanPanel extends JPanel {
    
    private MainFrame mainFrame;
    private RecoveryPlanService recoveryPlanService;
    private StudentService studentService;
    private CourseService courseService;
    private EmailService emailService;
    
    private JTable planTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    
    public RecoveryPlanPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.recoveryPlanService = RecoveryPlanService.getInstance();
        this.studentService = StudentService.getInstance();
        this.courseService = CourseService.getInstance();
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
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("Course Recovery Plans");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        String[] statuses = {"All", "Active", "Completed", "Cancelled"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setPreferredSize(new Dimension(120, 35));
        statusFilter.addActionListener(e -> filterTable());
        actionsPanel.add(new JLabel("Status: "));
        actionsPanel.add(statusFilter);
        
        JButton createButton = createButton("New Plan", new Color(46, 204, 113));
        createButton.addActionListener(e -> showCreatePlanDialog());
        actionsPanel.add(createButton);
        
        JButton refreshButton = createButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> refresh());
        actionsPanel.add(refreshButton);
        
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        String[] columns = {"Plan ID", "Student", "Course", "Start Date", "End Date", "Progress", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        planTable = new JTable(tableModel);
        planTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        planTable.setRowHeight(45);
        planTable.setShowGrid(false);
        planTable.setIntercellSpacing(new Dimension(0, 0));
        planTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        planTable.getTableHeader().setBackground(new Color(245, 247, 250));
        planTable.getTableHeader().setForeground(new Color(100, 100, 100));
        planTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        planTable.getColumn("Actions").setCellRenderer(new PlanButtonRenderer());
        planTable.getColumn("Actions").setCellEditor(new PlanButtonEditor(new JCheckBox()));
        
        planTable.getColumn("Progress").setCellRenderer(new ProgressRenderer());
        
        JScrollPane scrollPane = new JScrollPane(planTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        List<RecoveryPlan> plans = recoveryPlanService.getAllRecoveryPlans();
        
        for (RecoveryPlan plan : plans) {
            Student student = studentService.findById(plan.getStudentId());
            Course course = courseService.findById(plan.getCourseId());
            
            Object[] row = {
                plan.getPlanId(),
                student != null ? student.getFullName() : plan.getStudentId(),
                course != null ? course.getCourseName() : plan.getCourseId(),
                plan.getStartDate() != null ? plan.getStartDate().toString() : "N/A",
                plan.getEndDate() != null ? plan.getEndDate().toString() : "N/A",
                plan.getProgressPercentage(),
                plan.getStatus(),
                "Actions"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterTable() {
        String status = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        List<RecoveryPlan> plans = recoveryPlanService.getAllRecoveryPlans();
        
        for (RecoveryPlan plan : plans) {
            if ("All".equals(status) || plan.getStatus().equals(status)) {
                Student student = studentService.findById(plan.getStudentId());
                Course course = courseService.findById(plan.getCourseId());
                
                Object[] row = {
                    plan.getPlanId(),
                    student != null ? student.getFullName() : plan.getStudentId(),
                    course != null ? course.getCourseName() : plan.getCourseId(),
                    plan.getStartDate() != null ? plan.getStartDate().toString() : "N/A",
                    plan.getEndDate() != null ? plan.getEndDate().toString() : "N/A",
                    plan.getProgressPercentage(),
                    plan.getStatus(),
                    "Actions"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showCreatePlanDialog() {
        List<Student> studentsWithFailed = studentService.getStudentsWithFailedCourses();
        if (studentsWithFailed.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students with failed courses found.");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Recovery Plan", true);
        dialog.setSize(550, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JComboBox<String> studentCombo = new JComboBox<>();
        for (Student s : studentsWithFailed) {
            studentCombo.addItem(s.getStudentId() + " - " + s.getFullName());
        }
        addFormRow(formPanel, gbc, 0, "Student:", studentCombo);
        
        JComboBox<String> courseCombo = new JComboBox<>();
        studentCombo.addActionListener(e -> {
            courseCombo.removeAllItems();
            int idx = studentCombo.getSelectedIndex();
            if (idx >= 0) {
                Student selected = studentsWithFailed.get(idx);
                List<StudentCourse> failed = selected.getFailedCourses();
                for (StudentCourse sc : failed) {
                    courseCombo.addItem(sc.getCourse().getCourseId() + " - " + sc.getCourse().getCourseName());
                }
            }
        });
        if (studentCombo.getItemCount() > 0) {
            studentCombo.setSelectedIndex(0);
        }
        addFormRow(formPanel, gbc, 1, "Failed Course:", courseCombo);
        
        JTextArea recommendationArea = new JTextArea(4, 25);
        recommendationArea.setLineWrap(true);
        recommendationArea.setWrapStyleWord(true);
        JScrollPane recScroll = new JScrollPane(recommendationArea);
        addFormRow(formPanel, gbc, 2, "Recommendation:", recScroll);
        
        JTextField startDateField = new JTextField(15);
        startDateField.setText(LocalDate.now().toString());
        addFormRow(formPanel, gbc, 3, "Start Date (YYYY-MM-DD):", startDateField);
        
        JTextField endDateField = new JTextField(15);
        endDateField.setText(LocalDate.now().plusWeeks(4).toString());
        addFormRow(formPanel, gbc, 4, "End Date (YYYY-MM-DD):", endDateField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = createButton("Create", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                int studentIdx = studentCombo.getSelectedIndex();
                int courseIdx = courseCombo.getSelectedIndex();
                
                if (studentIdx < 0 || courseIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Please select student and course.");
                    return;
                }
                
                Student student = studentsWithFailed.get(studentIdx);
                StudentCourse failedCourse = student.getFailedCourses().get(courseIdx);
                
                RecoveryPlan plan = recoveryPlanService.createRecoveryPlan(
                    student.getStudentId(),
                    failedCourse.getCourse().getCourseId(),
                    recommendationArea.getText().trim(),
                    LocalDate.parse(startDateField.getText().trim()),
                    LocalDate.parse(endDateField.getText().trim()),
                    mainFrame.getCurrentUser().getUsername()
                );
                
                emailService.sendRecoveryPlanEmail(student, plan, failedCourse.getCourse());
                
                JOptionPane.showMessageDialog(dialog, 
                    "Recovery plan created successfully!\nEmail sent to student.");
                dialog.dispose();
                refresh();
                
                showAddMilestonesDialog(plan.getPlanId());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showAddMilestonesDialog(String planId) {
        RecoveryPlan plan = recoveryPlanService.findById(planId);
        if (plan == null) return;
        
        int addMore = JOptionPane.showConfirmDialog(this, 
            "Would you like to add milestones to this plan?", 
            "Add Milestones", JOptionPane.YES_NO_OPTION);
        
        if (addMore != JOptionPane.YES_OPTION) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Add Milestones - " + planId, true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel milestonesPanel = new JPanel();
        milestonesPanel.setLayout(new BoxLayout(milestonesPanel, BoxLayout.Y_AXIS));
        milestonesPanel.setBackground(Color.WHITE);
        milestonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] defaultWeeks = {"Week 1-2", "Week 3", "Week 4"};
        String[] defaultTasks = {"Review all lecture topics", "Meeting with module lecturer", "Take recovery exam"};
        
        java.util.List<JPanel> milestoneRows = new java.util.ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            JPanel row = createMilestoneRow(i + 1, defaultWeeks[i], defaultTasks[i]);
            milestoneRows.add(row);
            milestonesPanel.add(row);
            milestonesPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(milestonesPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton addRowButton = new JButton("Add More");
        addRowButton.addActionListener(e -> {
            int num = milestoneRows.size() + 1;
            JPanel row = createMilestoneRow(num, "Week " + num, "");
            milestoneRows.add(row);
            milestonesPanel.add(row);
            milestonesPanel.add(Box.createVerticalStrut(10));
            milestonesPanel.revalidate();
            milestonesPanel.repaint();
        });
        buttonPanel.add(addRowButton);
        
        JButton saveButton = createButton("Save Milestones", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            for (JPanel row : milestoneRows) {
                JTextField weekField = (JTextField) ((JPanel) row.getComponent(0)).getComponent(1);
                JTextField taskField = (JTextField) ((JPanel) row.getComponent(1)).getComponent(1);
                
                String week = weekField.getText().trim();
                String task = taskField.getText().trim();
                
                if (!week.isEmpty() && !task.isEmpty()) {
                    LocalDate dueDate = plan.getStartDate().plusWeeks(milestoneRows.indexOf(row) + 1);
                    recoveryPlanService.addMilestone(planId, week, task, "", dueDate);
                }
            }
            
            JOptionPane.showMessageDialog(dialog, "Milestones added successfully!");
            dialog.dispose();
            refresh();
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private JPanel createMilestoneRow(int num, String defaultWeek, String defaultTask) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBackground(new Color(250, 250, 250));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel weekPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        weekPanel.setOpaque(false);
        weekPanel.add(new JLabel("Study Week:"));
        JTextField weekField = new JTextField(defaultWeek, 15);
        weekPanel.add(weekField);
        row.add(weekPanel);
        
        JPanel taskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        taskPanel.setOpaque(false);
        taskPanel.add(new JLabel("Task:"));
        JTextField taskField = new JTextField(defaultTask, 30);
        taskPanel.add(taskField);
        row.add(taskPanel);
        
        return row;
    }
    
    private void showPlanDetails(String planId) {
        RecoveryPlan plan = recoveryPlanService.findById(planId);
        if (plan == null) return;
        
        Student student = studentService.findById(plan.getStudentId());
        Course course = courseService.findById(plan.getCourseId());
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Recovery Plan Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        addInfoRow(infoPanel, gbc, 0, "Plan ID:", plan.getPlanId());
        addInfoRow(infoPanel, gbc, 1, "Student:", student != null ? student.getFullName() : "N/A");
        addInfoRow(infoPanel, gbc, 2, "Course:", course != null ? course.getCourseName() : "N/A");
        addInfoRow(infoPanel, gbc, 3, "Status:", plan.getStatus());
        addInfoRow(infoPanel, gbc, 4, "Progress:", String.format("%.1f%%", plan.getProgressPercentage()));
        addInfoRow(infoPanel, gbc, 5, "Duration:", plan.getStartDate() + " to " + plan.getEndDate());
        addInfoRow(infoPanel, gbc, 6, "Recommendation:", plan.getRecommendation());
        
        dialog.add(infoPanel, BorderLayout.NORTH);
        
        JPanel milestonesPanel = new JPanel(new BorderLayout());
        milestonesPanel.setBorder(BorderFactory.createTitledBorder("Milestones"));
        milestonesPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Week", "Task", "Status", "Grade", "Actions"};
        DefaultTableModel milestonesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JTable milestonesTable = new JTable(milestonesModel);
        milestonesTable.setRowHeight(35);
        
        for (Milestone m : plan.getMilestones()) {
            Object[] row = {
                m.getStudyWeek(),
                m.getTask(),
                m.getStatus(),
                "Completed".equals(m.getStatus()) ? String.format("%.1f", m.getGrade()) : "-",
                "Grade"
            };
            milestonesModel.addRow(row);
        }
        
        milestonesTable.getColumn("Actions").setCellRenderer(new DefaultTableCellRenderer() {
            JButton button = new JButton("Grade");
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                return button;
            }
        });
        
        milestonesTable.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            JButton button = new JButton("Grade");
            int selectedRow;
            
            {
                button.addActionListener(e -> {
                    fireEditingStopped();
                    if (selectedRow >= 0 && selectedRow < plan.getMilestones().size()) {
                        Milestone m = plan.getMilestones().get(selectedRow);
                        showGradeMilestoneDialog(planId, m.getMilestoneId(), dialog);
                        showPlanDetails(planId);
                        dialog.dispose();
                    }
                });
            }
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                selectedRow = row;
                return button;
            }
        });
        
        milestonesPanel.add(new JScrollPane(milestonesTable), BorderLayout.CENTER);
        dialog.add(milestonesPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showGradeMilestoneDialog(String planId, String milestoneId, JDialog parentDialog) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Grade Milestone", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField gradeField = new JTextField(10);
        addFormRow(formPanel, gbc, 0, "Grade (0-100):", gradeField);
        
        JTextArea feedbackArea = new JTextArea(3, 20);
        feedbackArea.setLineWrap(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        addFormRow(formPanel, gbc, 1, "Feedback:", feedbackScroll);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = createButton("Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                double grade = Double.parseDouble(gradeField.getText().trim());
                String feedback = feedbackArea.getText().trim();
                
                recoveryPlanService.gradeMilestone(planId, milestoneId, grade, feedback);
                
                JOptionPane.showMessageDialog(dialog, "Grade saved successfully!");
                dialog.dispose();
                refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid grade.");
            }
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lbl, gbc);
        
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(val, gbc);
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        panel.add(component, gbc);
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    class ProgressRenderer extends JPanel implements TableCellRenderer {
        private JProgressBar progressBar;
        
        public ProgressRenderer() {
            setLayout(new BorderLayout(5, 0));
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
            
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            add(progressBar, BorderLayout.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            double progress = (Double) value;
            progressBar.setValue((int) progress);
            progressBar.setString(String.format("%.0f%%", progress));
            
            if (progress >= 100) {
                progressBar.setForeground(new Color(46, 204, 113));
            } else if (progress >= 50) {
                progressBar.setForeground(new Color(241, 196, 15));
            } else {
                progressBar.setForeground(new Color(52, 152, 219));
            }
            
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }
    
    class PlanButtonRenderer extends JPanel implements TableCellRenderer {
        public PlanButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 5));
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            JButton viewBtn = new JButton("View");
            viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            viewBtn.setPreferredSize(new Dimension(50, 24));
            add(viewBtn);
            
            return this;
        }
    }
    
    class PlanButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private String planId;
        
        public PlanButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 5));
            
            JButton viewButton = new JButton("View");
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            viewButton.setPreferredSize(new Dimension(50, 24));
            viewButton.addActionListener(e -> {
                fireEditingStopped();
                showPlanDetails(planId);
            });
            
            panel.add(viewButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            planId = (String) table.getValueAt(row, 0);
            panel.setBackground(Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
