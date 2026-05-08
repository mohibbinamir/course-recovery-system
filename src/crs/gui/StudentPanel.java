package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    
    private MainFrame mainFrame;
    private StudentService studentService;
    private CourseService courseService;
    
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.studentService = StudentService.getInstance();
        this.courseService = CourseService.getInstance();
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
        
        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable();
            }
        });
        actionsPanel.add(searchField);
        
        JButton importButton = createButton("Import CSV", new Color(155, 89, 182));
        importButton.addActionListener(e -> importStudentsFromCSV());
        actionsPanel.add(importButton);
        
        JButton importGradesButton = createButton("Import Grades", new Color(241, 196, 15));
        importGradesButton.addActionListener(e -> importGradesFromFile());
        actionsPanel.add(importGradesButton);
        
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
        
        String[] columns = {"Student ID", "Name", "Major", "Year", "Email", "CGPA", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(40);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(new Color(245, 247, 250));
        studentTable.getTableHeader().setForeground(new Color(100, 100, 100));
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        studentTable.getColumn("Actions").setCellRenderer(new StudentButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new StudentButtonEditor(new JCheckBox()));
        
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 6);
                    if ("Not Eligible".equals(status)) {
                        c.setBackground(new Color(255, 235, 235));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        List<Student> students = studentService.getAllStudents();
        
        for (Student student : students) {
            double cgpa = studentService.calculateCGPA(student.getStudentId());
            String status = student.isEligibleToProgress() ? "Eligible" : "Not Eligible";
            
            Object[] row = {
                student.getStudentId(),
                student.getFullName(),
                student.getMajor(),
                student.getYear(),
                student.getEmail(),
                String.format("%.2f", cgpa),
                status,
                "Actions"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterTable() {
        String search = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        List<Student> students = studentService.searchStudents(search);
        
        for (Student student : students) {
            double cgpa = studentService.calculateCGPA(student.getStudentId());
            String status = student.isEligibleToProgress() ? "Eligible" : "Not Eligible";
            
            Object[] row = {
                student.getStudentId(),
                student.getFullName(),
                student.getMajor(),
                student.getYear(),
                student.getEmail(),
                String.format("%.2f", cgpa),
                status,
                "Actions"
            };
            tableModel.addRow(row);
        }
    }

private void importStudentsFromCSV() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Student File (.csv or .txt)");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV or Text Files (*.csv, *.txt)", "csv", "txt"));

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();

        try {
            int imported = studentService.importStudentsFromFile(filePath);
            refresh();

            if (imported > 0) {
                JOptionPane.showMessageDialog(this, "Students imported successfully! (" + imported + " added)");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No students were imported.\n\n" +
                    "Expected columns (6):\n" +
                    "StudentID, FirstName, LastName, Major, Year, Email\n\n" +
                    "Supported delimiters: comma, |, tab, ;",
                    "Import Warning",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Import failed: " + ex.getMessage(),
                "Import Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

private void importGradesFromFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select grades file (.csv or .txt)");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV/TXT Files", "csv", "txt"));

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        try {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            int imported = studentService.importGradesFromFile(path, courseService);
            JOptionPane.showMessageDialog(this, "Imported " + imported + " grade records successfully!");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error importing grades: " + ex.getMessage(),
                "Import Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    private void showStudentDetails(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Student Details - " + student.getFullName(), true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        addInfoRow(infoPanel, gbc, 0, "Student ID:", student.getStudentId());
        addInfoRow(infoPanel, gbc, 1, "Name:", student.getFullName());
        addInfoRow(infoPanel, gbc, 2, "Email:", student.getEmail());
        addInfoRow(infoPanel, gbc, 3, "Major:", student.getMajor());
        addInfoRow(infoPanel, gbc, 4, "Year:", student.getYear());
        addInfoRow(infoPanel, gbc, 5, "Program:", student.getProgram());
        addInfoRow(infoPanel, gbc, 6, "CGPA:", String.format("%.2f", studentService.calculateCGPA(studentId)));
        addInfoRow(infoPanel, gbc, 7, "Status:", student.isEligibleToProgress() ? "Eligible" : "Not Eligible");
        
        dialog.add(infoPanel, BorderLayout.NORTH);
        
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));
        coursesPanel.setBackground(Color.WHITE);
        
        String[] columns = {"Course ID", "Course Name", "Credits", "Grade", "Status"};
        DefaultTableModel coursesModel = new DefaultTableModel(columns, 0);
        JTable coursesTable = new JTable(coursesModel);
        coursesTable.setRowHeight(30);
        
        List<StudentCourse> courses = studentService.getStudentCourses(studentId);
        for (StudentCourse sc : courses) {
            Object[] row = {
                sc.getCourse().getCourseId(),
                sc.getCourse().getCourseName(),
                sc.getCourse().getCredits(),
                sc.getGrade() != null ? sc.getGrade() : "N/A",
                sc.getStatus()
            };
            coursesModel.addRow(row);
        }
        
        coursesPanel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        dialog.add(coursesPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showAssignGradeDialog(String studentId) {
        Student student = studentService.findById(studentId);
        if (student == null) return;
        
        List<StudentCourse> courses = studentService.getStudentCourses(studentId);
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses enrolled for this student.");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Assign Grade - " + student.getFullName(), true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(courseLabel, gbc);
        
        JComboBox<String> courseCombo = new JComboBox<>();
        for (StudentCourse sc : courses) {
            courseCombo.addItem(sc.getCourse().getCourseId() + " - " + sc.getCourse().getCourseName());
        }
        gbc.gridx = 1;
        formPanel.add(courseCombo, gbc);
        
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        JComboBox<String> gradeCombo = new JComboBox<>(grades);
        addFormRow(formPanel, gbc, 1, "Grade:", gradeCombo);
        
        JTextField examField = new JTextField(10);
        addFormRow(formPanel, gbc, 2, "Exam Score:", examField);
        
        JTextField assignmentField = new JTextField(10);
        addFormRow(formPanel, gbc, 3, "Assignment Score:", assignmentField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = createButton("Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                int selectedIndex = courseCombo.getSelectedIndex();
                StudentCourse selectedCourse = courses.get(selectedIndex);
                String grade = (String) gradeCombo.getSelectedItem();
                double examScore = Double.parseDouble(examField.getText().trim());
                double assignmentScore = Double.parseDouble(assignmentField.getText().trim());
                
                studentService.assignGrade(studentId, selectedCourse.getCourse().getCourseId(), 
                    grade, examScore, assignmentScore);
                
                JOptionPane.showMessageDialog(dialog, "Grade assigned successfully!");
                dialog.dispose();
                refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid scores.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(val, gbc);
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lbl, gbc);
        
        component.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        panel.add(component, gbc);
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
    
    class StudentButtonRenderer extends JPanel implements TableCellRenderer {
        public StudentButtonRenderer() {
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
            
            JButton gradeBtn = new JButton("Grade");
            gradeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            gradeBtn.setPreferredSize(new Dimension(50, 24));
            add(gradeBtn);
            
            return this;
        }
    }
    
    class StudentButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private String studentId;
        
        public StudentButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 5));
            
            JButton viewButton = new JButton("View");
            viewButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            viewButton.setPreferredSize(new Dimension(50, 24));
            viewButton.addActionListener(e -> {
                fireEditingStopped();
                showStudentDetails(studentId);
            });
            
            JButton gradeButton = new JButton("Grade");
            gradeButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            gradeButton.setPreferredSize(new Dimension(50, 24));
            gradeButton.addActionListener(e -> {
                fireEditingStopped();
                showAssignGradeDialog(studentId);
            });
            
            panel.add(viewButton);
            panel.add(gradeButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            studentId = (String) table.getValueAt(row, 0);
            panel.setBackground(Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
