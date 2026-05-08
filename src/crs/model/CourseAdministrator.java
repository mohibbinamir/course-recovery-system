package crs.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CourseAdministrator extends User {
    private static final long serialVersionUID = 1L;
    
    private static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList(
        "VIEW_STUDENTS", "VIEW_COURSES", "UPDATE_COURSES",
        "VIEW_GRADES", "UPDATE_GRADES",
        "VIEW_ELIGIBILITY",
        "VIEW_RECOVERY_PLANS", "CREATE_RECOVERY_PLAN", "UPDATE_RECOVERY_PLAN", "DELETE_RECOVERY_PLAN",
        "MONITOR_PROGRESS", "ENTER_GRADES",
        "GENERATE_REPORTS", "EXPORT_PDF",
        "SEND_NOTIFICATIONS"
    ));
    
    private String assignedProgram;
    
    public CourseAdministrator() {
        super();
    }
    
    public CourseAdministrator(String userId, String username, String password, String email, String fullName) {
        super(userId, username, password, email, fullName);
    }
    
    @Override
    public String getRole() {
        return "Course Administrator";
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return PERMISSIONS.contains(permission);
    }
    
    public String getAssignedProgram() { return assignedProgram; }
    public void setAssignedProgram(String assignedProgram) { this.assignedProgram = assignedProgram; }
}
