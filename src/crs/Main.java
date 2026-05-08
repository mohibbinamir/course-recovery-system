package crs;

import crs.gui.MainFrame;
import crs.service.*;

import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("Starting Course Recovery System...");
        
        initializeServices();
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                UIManager.put("Button.arc", 5);
                UIManager.put("Component.arc", 5);
                UIManager.put("TextComponent.arc", 5);
                
            } catch (Exception e) {
                System.err.println("Could not set look and feel: " + e.getMessage());
            }
            
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
    
    private static void initializeServices() {
        UserService.getInstance();
        StudentService.getInstance();
        CourseService.getInstance();
        RecoveryPlanService.getInstance();
        ReportService.getInstance();
        EmailService.getInstance();
        PDFService.getInstance();
        
        System.out.println("Services initialized successfully.");
    }
}
