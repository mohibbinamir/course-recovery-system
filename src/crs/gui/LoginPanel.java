package crs.gui;

import crs.model.User;
import crs.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private MainFrame mainFrame;
    private UserService userService;
    
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = UserService.getInstance();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        
        JLabel titleLabel = new JLabel("Course Recovery System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 65, 114));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Educational Institution Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(subtitleLabel);
        
        loginCard.add(Box.createVerticalStrut(30));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(8, 5, 8, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userLabel, formGbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formPanel.add(usernameField, formGbc);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(passLabel, formGbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formPanel.add(passwordField, formGbc);
        
        loginCard.add(formPanel);
        loginCard.add(Box.createVerticalStrut(20));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(41, 65, 114));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.setMaximumSize(new Dimension(250, 40));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(30, 50, 90));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(41, 65, 114));
            }
        });
        
        loginButton.addActionListener(e -> performLogin());
        loginCard.add(loginButton);
        
        loginCard.add(Box.createVerticalStrut(15));
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(statusLabel);
        
        loginCard.add(Box.createVerticalStrut(20));
        
        JLabel defaultCreds = new JLabel("<html><center>Default Credentials:<br/>Admin: admin / admin123<br/>Course Admin: courseadmin / course123</center></html>");
        defaultCreds.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        defaultCreds.setForeground(new Color(120, 120, 120));
        defaultCreds.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(defaultCreds);
        
        add(loginCard, gbc);
        
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }
        
        User user = userService.authenticate(username, password);
        
        if (user != null) {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Login successful! Welcome, " + user.getFullName());
            mainFrame.onLoginSuccess(user);
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }
    
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
