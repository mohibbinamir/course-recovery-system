package crs.gui;

import crs.model.*;
import crs.service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    
    private MainFrame mainFrame;
    private UserService userService;
    private EmailService emailService;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public UserManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userService = UserService.getInstance();
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
        
        JLabel titleLabel = new JLabel("User Management");
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
        
        JButton addButton = createButton("Add User", new Color(46, 204, 113));
        addButton.addActionListener(e -> showAddUserDialog());
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
        
        String[] columns = {"User ID", "Username", "Full Name", "Email", "Role", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userTable.setRowHeight(40);
        userTable.setShowGrid(false);
        userTable.setIntercellSpacing(new Dimension(0, 0));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.getTableHeader().setBackground(new Color(245, 247, 250));
        userTable.getTableHeader().setForeground(new Color(100, 100, 100));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        userTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive() ? "Active" : "Inactive",
                "Actions"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterTable() {
        String search = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(search) ||
                user.getFullName().toLowerCase().contains(search) ||
                user.getEmail().toLowerCase().contains(search)) {
                Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive",
                    "Actions"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField usernameField = addFormField(formPanel, "Username:", gbc, 0);
        JPasswordField passwordField = new JPasswordField(20);
        addFormFieldWithComponent(formPanel, "Password:", passwordField, gbc, 1);
        JTextField fullNameField = addFormField(formPanel, "Full Name:", gbc, 2);
        JTextField emailField = addFormField(formPanel, "Email:", gbc, 3);
        
        String[] roles = {"Academic Officer", "Course Administrator"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        addFormFieldWithComponent(formPanel, "Role:", roleCombo, gbc, 4);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = createButton("Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            try {
                String password = new String(passwordField.getPassword());
                User user = userService.createUser(
                    usernameField.getText().trim(),
                    password,
                    emailField.getText().trim(),
                    fullNameField.getText().trim(),
                    (String) roleCombo.getSelectedItem()
                );
                
                emailService.sendAccountCreatedEmail(user, password);
                
                JOptionPane.showMessageDialog(dialog, 
                    "User created successfully!\nEmail notification sent.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditUserDialog(String userId) {
        User user = userService.findById(userId);
        if (user == null) return;
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
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
        
        JTextField fullNameField = addFormField(formPanel, "Full Name:", gbc, 0);
        fullNameField.setText(user.getFullName());
        
        JTextField emailField = addFormField(formPanel, "Email:", gbc, 1);
        emailField.setText(user.getEmail());
        
        JCheckBox activeCheck = new JCheckBox("Active", user.isActive());
        addFormFieldWithComponent(formPanel, "Status:", activeCheck, gbc, 2);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = createButton("Save", new Color(46, 204, 113));
        saveButton.addActionListener(e -> {
            user.setFullName(fullNameField.getText().trim());
            user.setEmail(emailField.getText().trim());
            user.setActive(activeCheck.isSelected());
            userService.updateUser(user);
            
            JOptionPane.showMessageDialog(dialog, "User updated successfully!");
            dialog.dispose();
            refresh();
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
        gbc.gridwidth = 1;
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
        gbc.gridwidth = 1;
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
    
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            JButton editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editBtn.setPreferredSize(new Dimension(55, 25));
            add(editBtn);
            
            JButton resetBtn = new JButton("Reset");
            resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            resetBtn.setPreferredSize(new Dimension(55, 25));
            add(resetBtn);
            
            return this;
        }
    }
    
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton resetButton;
        private String userId;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            
            editButton = new JButton("Edit");
            editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editButton.setPreferredSize(new Dimension(55, 25));
            editButton.addActionListener(e -> {
                fireEditingStopped();
                showEditUserDialog(userId);
            });
            
            resetButton = new JButton("Reset");
            resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            resetButton.setPreferredSize(new Dimension(55, 25));
            resetButton.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Reset password for this user?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String newPass = userService.resetPassword(userId);
                    User user = userService.findById(userId);
                    if (user != null && newPass != null) {
                        emailService.sendPasswordResetEmail(user, newPass);
                        JOptionPane.showMessageDialog(panel, 
                            "Password reset to: " + newPass + "\nEmail notification sent.");
                    }
                }
            });
            
            panel.add(editButton);
            panel.add(resetButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            userId = (String) table.getValueAt(row, 0);
            panel.setBackground(Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}
