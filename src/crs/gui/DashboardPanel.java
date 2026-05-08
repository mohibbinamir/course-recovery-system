package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {
    
    private MainFrame mainFrame;
    private User currentUser;
    private StudentService studentService;
    private CourseService courseService;
    private RecoveryPlanService recoveryPlanService;
    
    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.studentService = StudentService.getInstance();
        this.courseService = CourseService.getInstance();
        this.recoveryPlanService = RecoveryPlanService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
    }
    
    public void refresh(User user) {
        this.currentUser = user;
        removeAll();
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel quickActionsPanel = createQuickActionsPanel();
        contentPanel.add(quickActionsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel alertsPanel = createAlertsPanel();
        contentPanel.add(alertsPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 65, 114));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel);
        
        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(200, 220, 255));
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(roleLabel);
        
        if (currentUser.getLastLogin() != null) {
            String lastLogin = currentUser.getLastLogin().format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            JLabel loginLabel = new JLabel("Last login: " + lastLogin);
            loginLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            loginLabel.setForeground(new Color(180, 200, 240));
            leftPanel.add(Box.createVerticalStrut(5));
            leftPanel.add(loginLabel);
        }
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        int totalStudents = studentService.getAllStudents().size();
        int totalCourses = courseService.getAllCourses().size();
        int activePlans = recoveryPlanService.getActivePlans().size();
        int ineligibleStudents = studentService.getIneligibleStudents().size();
        
        panel.add(createStatCard("Total Students", String.valueOf(totalStudents), 
            new Color(52, 152, 219)));
        panel.add(createStatCard("Total Courses", String.valueOf(totalCourses), 
            new Color(46, 204, 113)));
        panel.add(createStatCard("Active Recovery Plans", String.valueOf(activePlans), 
            new Color(241, 196, 15)));
        panel.add(createStatCard("Ineligible Students", String.valueOf(ineligibleStudents), 
            new Color(231, 76, 60)));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(4, 0, 0, 0, color),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(new Color(50, 50, 50));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(120, 120, 120));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        
        return card;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonsPanel.setOpaque(false);
        
        if (currentUser.hasPermission("VIEW_STUDENTS")) {
            buttonsPanel.add(createActionButton("View Students", 
                new Color(52, 152, 219), e -> mainFrame.showPanel("students")));
        }
        if (currentUser.hasPermission("VIEW_RECOVERY_PLANS")) {
            buttonsPanel.add(createActionButton("Recovery Plans", 
                new Color(155, 89, 182), e -> mainFrame.showPanel("recovery")));
        }
        if (currentUser.hasPermission("VIEW_ELIGIBILITY")) {
            buttonsPanel.add(createActionButton("Check Eligibility", 
                new Color(46, 204, 113), e -> mainFrame.showPanel("eligibility")));
        }
        if (currentUser.hasPermission("GENERATE_REPORTS")) {
            buttonsPanel.add(createActionButton("Generate Report", 
                new Color(241, 196, 15), e -> mainFrame.showPanel("reports")));
        }
        if (currentUser.hasPermission("CREATE_USER")) {
            buttonsPanel.add(createActionButton("Manage Users", 
                new Color(231, 76, 60), e -> mainFrame.showPanel("users")));
        }
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
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
    
    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        JLabel titleLabel = new JLabel("Recent Alerts & Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel alertsContent = new JPanel();
        alertsContent.setLayout(new BoxLayout(alertsContent, BoxLayout.Y_AXIS));
        alertsContent.setOpaque(false);
        alertsContent.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        List<Student> ineligible = studentService.getIneligibleStudents();
        if (!ineligible.isEmpty()) {
            alertsContent.add(createAlertItem(
                "Warning: " + ineligible.size() + " students are not eligible to progress",
                new Color(231, 76, 60)));
        }
        
        List<RecoveryPlan> activePlans = recoveryPlanService.getActivePlans();
        for (int i = 0; i < Math.min(3, activePlans.size()); i++) {
            RecoveryPlan plan = activePlans.get(i);
            Student student = studentService.findById(plan.getStudentId());
            String studentName = student != null ? student.getFullName() : plan.getStudentId();
            alertsContent.add(createAlertItem(
                "Active recovery plan for " + studentName + " - " + 
                String.format("%.0f%%", plan.getProgressPercentage()) + " complete",
                new Color(241, 196, 15)));
        }
        
        if (alertsContent.getComponentCount() == 0) {
            alertsContent.add(createAlertItem("No alerts at this time", new Color(46, 204, 113)));
        }
        
        panel.add(alertsContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAlertItem(String message, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JPanel indicator = new JPanel();
        indicator.setBackground(color);
        indicator.setPreferredSize(new Dimension(4, 30));
        item.add(indicator, BorderLayout.WEST);
        
        JLabel label = new JLabel("  " + message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(70, 70, 70));
        item.add(label, BorderLayout.CENTER);
        
        return item;
    }
}
