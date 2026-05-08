package crs.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AcademicOfficer extends User {
    private static final long serialVersionUID = 1L;
    
    private static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(
        "VIEW_USERS", "CREATE_USER", "UPDATE_USER", "DEACTIVATE_USER",
        "VIEW_STUDENTS", "VIEW_COURSES", "VIEW_GRADES",
        "VIEW_ELIGIBILITY", "MANAGE_ENROLMENT",
        "VIEW_RECOVERY_PLANS", "CREATE_RECOVERY_PLAN", "UPDATE_RECOVERY_PLAN",
        "MONITOR_PROGRESS", "ENTER_GRADES",
        "GENERATE_REPORTS", "EXPORT_PDF",
        "SEND_NOTIFICATIONS",
        "RESET_PASSWORD"
    ));
    
    private String department;
    private String officeNumber;
    
    public AcademicOfficer() {
        super();
    }
    
    public AcademicOfficer(String userId, String username, String password, String email, String fullName) {
        super(userId, username, password, email, fullName);
    }
    
    @Override
    public String getRole() {
        return "Academic Officer";
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return PERMISSIONS.contains(permission);
    }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
}
