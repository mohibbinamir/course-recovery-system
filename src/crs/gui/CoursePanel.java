package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {
    
    private MainFrame mainFrame;
    private CourseService courseService;
    
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public CoursePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
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
        
        JLabel titleLabel = new JLabel("Course Management");
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
        importButton.addActionListener(e -> importCoursesFromCSV());
        actionsPanel.add(importButton);
        
        JButton addButton = createButton("Add Course", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddCourseDialog());
        actionsPanel.add(addButton);
        
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
        
        String[] columns = {"Course ID", "Course Name", "Credits", "Semester", "Instructor", "Exam %", "Assignment %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseTable.setRowHeight(40);
        courseTable.setShowGrid(false);
        courseTable.setIntercellSpacing(new Dimension(0, 0));
        courseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        courseTable.getTableHeader().setBackground(new Color(245, 247, 250));
        courseTable.getTableHeader().setForeground(new Color(100, 100, 100));
        courseTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        courseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                return c;
            }
        });
        
        courseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = courseTable.getSelectedRow();
                    if (row >= 0) {
                        String courseId = (String) tableModel.getValueAt(row, 0);
                        showEditCourseDialog(courseId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        List<Course> courses = courseService.getAllCourses();
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseName(),
                course.getCredits(),
                course.getSemester(),
                course.getInstructor(),
                course.getExamWeight() + "%",
                course.getAssignmentWeight() + "%"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterTable() {
        String search = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        List<Course> courses = courseService.searchCourses(search);
        
        for (Course course : courses) {
            Object[] row = {
                course.getCourseId(),
                course.getCourseName(),
                course.getCredits(),
                course.getSemester(),
                course.getInstructor(),
                course.getExamWeight() + "%",
                course.getAssignmentWeight() + "%"
            };
            tableModel.addRow(row);
        }
    }

private void importCoursesFromCSV() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Course File (.csv or .txt)");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV or Text Files (*.csv, *.txt)", "csv", "txt"));

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();

        try {
            int imported = courseService.importCoursesFromFile(filePath);
            refresh();

            if (imported > 0) {
                JOptionPane.showMessageDialog(this, "Courses imported successfully! (" + imported + " added)");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "No courses were imported.\n\n" +
                    "Expected columns (7):\n" +
                    "CourseID, CourseName, Credits, Semester, Instructor, ExamWeight, AssignmentWeight\n\n" +
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

    private void showAddCourseDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Course", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField idField = addFormField(formPanel, "Course ID:", gbc, 0);
        JTextField nameField = addFormField(formPanel, "Course Name:", gbc, 1);
        JTextField creditsField = addFormField(formPanel, "Credits:", gbc, 2);
        
        String[] semesters = {"Fall", "Spring", "Summer"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        addFormFieldWithComponent(formPanel, "Semester:", semesterCombo, gbc, 3);
        
        JTextField instructorField = addFormField(formPanel, "Instructor:", gbc, 4);
        JTextField examWeightField = addFormField(formPanel, "Exam Weight (%):", gbc, 5);
        JTextField assignmentWeightField = addFormField(formPanel, "Assignment Weight (%):", gbc, 6);
        
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
                Course course = new Course(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    Integer.parseInt(creditsField.getText().trim()),
                    (String) semesterCombo.getSelectedItem(),
                    instructorField.getText().trim(),
                    Integer.parseInt(examWeightField.getText().trim()),
                    Integer.parseInt(assignmentWeightField.getText().trim())
                );
                
                courseService.addCourse(course);
                JOptionPane.showMessageDialog(dialog, "Course added successfully!");
                dialog.dispose();
                refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditCourseDialog(String courseId) {
        Course course = courseService.findById(courseId);
        if (course == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Course", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField nameField = addFormField(formPanel, "Course Name:", gbc, 0);
        nameField.setText(course.getCourseName());
        
        JTextField creditsField = addFormField(formPanel, "Credits:", gbc, 1);
        creditsField.setText(String.valueOf(course.getCredits()));
        
        String[] semesters = {"Fall", "Spring", "Summer"};
        JComboBox<String> semesterCombo = new JComboBox<>(semesters);
        semesterCombo.setSelectedItem(course.getSemester());
        addFormFieldWithComponent(formPanel, "Semester:", semesterCombo, gbc, 2);
        
        JTextField instructorField = addFormField(formPanel, "Instructor:", gbc, 3);
        instructorField.setText(course.getInstructor());
        
        JTextField examWeightField = addFormField(formPanel, "Exam Weight (%):", gbc, 4);
        examWeightField.setText(String.valueOf(course.getExamWeight()));
        
        JTextField assignmentWeightField = addFormField(formPanel, "Assignment Weight (%):", gbc, 5);
        assignmentWeightField.setText(String.valueOf(course.getAssignmentWeight()));
        
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
                course.setCourseName(nameField.getText().trim());
                course.setCredits(Integer.parseInt(creditsField.getText().trim()));
                course.setSemester((String) semesterCombo.getSelectedItem());
                course.setInstructor(instructorField.getText().trim());
                course.setExamWeight(Integer.parseInt(examWeightField.getText().trim()));
                course.setAssignmentWeight(Integer.parseInt(assignmentWeightField.getText().trim()));
                
                courseService.updateCourse(course);
                JOptionPane.showMessageDialog(dialog, "Course updated successfully!");
                dialog.dispose();
                refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private JTextField addFormField(JPanel panel, String label, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lbl, gbc);
        
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(250, 32));
        gbc.gridx = 1;
        panel.add(field, gbc);
        
        return field;
    }
    
    private void addFormFieldWithComponent(JPanel panel, String label, JComponent component, GridBagConstraints gbc, int row) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = row;
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
}
