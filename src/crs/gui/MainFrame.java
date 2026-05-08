package crs.gui;

import crs.model.User;
import crs.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private UserManagementPanel userPanel;
    private StudentPanel studentPanel;
    private CoursePanel coursePanel;
    private RecoveryPlanPanel recoveryPanel;
    private EligibilityPanel eligibilityPanel;
    private ReportPanel reportPanel;
    
    private User currentUser;
    private UserService userService;
    
    private JButton selectedButton;
    
    public MainFrame() {
        this.userService = UserService.getInstance();
        initFrame();
        initComponents();
    }
    
    private void initFrame() {
        setTitle("Course Recovery System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        
        loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);
        userPanel = new UserManagementPanel(this);
        studentPanel = new StudentPanel(this);
        coursePanel = new CoursePanel(this);
        recoveryPanel = new RecoveryPlanPanel(this);
        eligibilityPanel = new EligibilityPanel(this);
        reportPanel = new ReportPanel(this);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(userPanel, "users");
        contentPanel.add(studentPanel, "students");
        contentPanel.add(coursePanel, "courses");
        contentPanel.add(recoveryPanel, "recovery");
        contentPanel.add(eligibilityPanel, "eligibility");
        contentPanel.add(reportPanel, "reports");
        
        setContentPane(loginPanel);
    }
    
    public void onLoginSuccess(User user) {
        this.currentUser = user;
        
        mainPanel.removeAll();
        mainPanel.add(createTopBar(), BorderLayout.NORTH);
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        dashboardPanel.refresh(user);
        studentPanel.refresh();
        coursePanel.refresh();
        recoveryPanel.refresh();
        eligibilityPanel.refresh();
        reportPanel.refresh();
        
        if (currentUser.hasPermission("VIEW_USERS")) {
            userPanel.refresh();
        }
        
        showPanel("dashboard");
        
        revalidate();
        repaint();
    }
    
    public void logout() {
        userService.logout();
        currentUser = null;
        loginPanel.reset();
        setContentPane(loginPanel);
        revalidate();
        repaint();
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(41, 65, 114));
        topBar.setPreferredSize(new Dimension(0, 55));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JLabel titleLabel = new JLabel("Course Recovery System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);
        
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(52, 73, 94));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JButton dashboardBtn = createSidebarButton("Dashboard", "dashboard");
        sidebarPanel.add(dashboardBtn);
        selectedButton = dashboardBtn;
        dashboardBtn.setBackground(new Color(41, 58, 75));
        
        sidebarPanel.add(createSidebarButton("Students", "students"));
        sidebarPanel.add(createSidebarButton("Courses", "courses"));
        sidebarPanel.add(createSidebarButton("Recovery Plans", "recovery"));
        sidebarPanel.add(createSidebarButton("Eligibility", "eligibility"));
        sidebarPanel.add(createSidebarButton("Reports", "reports"));
        
        if (currentUser.hasPermission("CREATE_USER")) {
            sidebarPanel.add(Box.createVerticalStrut(20));
            JLabel adminLabel = new JLabel("  Administration");
            adminLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            adminLabel.setForeground(new Color(150, 160, 170));
            adminLabel.setMaximumSize(new Dimension(200, 30));
            sidebarPanel.add(adminLabel);
            sidebarPanel.add(createSidebarButton("User Management", "users"));
        }
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        return sidebarPanel;
    }
    
    private JButton createSidebarButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });
        
        button.addActionListener(e -> {
            if (selectedButton != null) {
                selectedButton.setBackground(new Color(52, 73, 94));
            }
            button.setBackground(new Color(41, 58, 75));
            selectedButton = button;
            showPanel(panelName);
        });
        
        return button;
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        switch (panelName) {
            case "dashboard":
                dashboardPanel.refresh(currentUser);
                break;
            case "students":
                studentPanel.refresh();
                break;
            case "courses":
                coursePanel.refresh();
                break;
            case "recovery":
                recoveryPanel.refresh();
                break;
            case "eligibility":
                eligibilityPanel.refresh();
                break;
            case "reports":
                reportPanel.refresh();
                break;
            case "users":
                if (currentUser.hasPermission("VIEW_USERS")) {
                    userPanel.refresh();
                }
                break;
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
