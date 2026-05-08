package crs.service;

import crs.model.*;
import crs.util.FileManager;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class StudentService {
    
    private static final String STUDENTS_FILE = "students.dat";
    private static final String STUDENT_COURSES_FILE = "student_courses.dat";
    
    private List<Student> students;
    private List<StudentCourse> studentCourses;
    
    private static StudentService instance;
    
    private StudentService() {
        loadStudents();
        loadStudentCourses();
    }
    
    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }
    
    private void loadStudents() {
        students = FileManager.loadFromBinaryFile(STUDENTS_FILE);
        if (students == null) {
            students = new ArrayList<>();
        }
    }
    
    private void loadStudentCourses() {
        studentCourses = FileManager.loadFromBinaryFile(STUDENT_COURSES_FILE);
        if (studentCourses == null) {
            studentCourses = new ArrayList<>();
        }
    }
    
    public void saveStudents() {
        FileManager.saveToBinaryFile(STUDENTS_FILE, students);
    }
    
    public void saveStudentCourses() {
        FileManager.saveToBinaryFile(STUDENT_COURSES_FILE, studentCourses);
    }
    
    /**
 * Imports students from a delimited text file (.csv or .txt).
 * Supported delimiters: comma (,), pipe (|), tab (	), semicolon (;)
 * Header row is optional.
 *
 * Expected columns:
 * StudentID, FirstName, LastName, Major, Year, Email
 *
 * @return number of newly imported students (duplicates are ignored)
 */
public int importStudentsFromFile(String filePath) {
    int imported = 0;
    int skipped = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Skip header if present
            String lower = line.toLowerCase();
            if (lower.startsWith("studentid") || lower.startsWith("student id")) {
                continue;
            }

            String delimiter = detectDelimiter(line);
            String[] parts = splitLine(line, delimiter);

            if (parts.length < 6) {
                skipped++;
                continue;
            }

            try {
                Student student = new Student(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    parts[5].trim(),
                    parts[3].trim(),
                    parts[4].trim()
                );

                if (findById(student.getStudentId()) == null) {
                    students.add(student);
                    imported++;
                }
            } catch (Exception ex) {
                skipped++;
            }
        }

        saveStudents();

        if (imported == 0 && skipped > 0) {
            System.err.println("Student import: 0 imported, " + skipped + " skipped. Check file delimiter/format.");
        }

        return imported;

    } catch (IOException e) {
        throw new RuntimeException("Error importing students: " + e.getMessage(), e);
    }
}

// Backward-compatible name (UI calls this)
public void importStudentsFromCSV(String filePath) {
    importStudentsFromFile(filePath);
}

private String detectDelimiter(String line) {
    String[] candidates = {",", "|", "	", ";"};
    int bestCount = 0;
    String best = ",";

    for (String c : candidates) {
        int count = countOccurrences(line, c.equals("	") ? "	" : c);
        if (count > bestCount) {
            bestCount = count;
            best = c;
        }
    }
    return best;
}

private int countOccurrences(String line, String delimiter) {
    if ("	".equals(delimiter)) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '	') count++;
        }
        return count;
    }
    int count = 0;
    int idx = 0;
    while ((idx = line.indexOf(delimiter, idx)) != -1) {
        count++;
        idx += delimiter.length();
    }
    return count;
}

private String[] splitLine(String line, String delimiter) {
    if ("	".equals(delimiter)) {
        return line.split("\t", -1);
    }
    return line.split(Pattern.quote(delimiter), -1);
}
    
    public Student addStudent(Student student) {
        if (findById(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Student ID already exists");
        }
        students.add(student);
        saveStudents();
        return student;
    }
    
    public void updateStudent(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(student.getStudentId())) {
                students.set(i, student);
                saveStudents();
                return;
            }
        }
    }
    
    public Student findById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }
    
    public List<Student> searchStudents(String keyword) {
        List<Student> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Student student : students) {
            if (student.getStudentId().toLowerCase().contains(lower) ||
                student.getFirstName().toLowerCase().contains(lower) ||
                student.getLastName().toLowerCase().contains(lower) ||
                student.getMajor().toLowerCase().contains(lower)) {
                results.add(student);
            }
        }
        return results;
    }
    
    public void enrollStudentInCourse(String studentId, Course course, String semester, int year) {
        StudentCourse sc = new StudentCourse(studentId, course, semester, year);
        studentCourses.add(sc);
        
        Student student = findById(studentId);
        if (student != null) {
            student.addCourse(sc);
            saveStudents();
        }
        saveStudentCourses();
    }
    
    public void updateStudentCourse(StudentCourse studentCourse) {
        for (int i = 0; i < studentCourses.size(); i++) {
            StudentCourse sc = studentCourses.get(i);
            if (sc.getStudentId().equals(studentCourse.getStudentId()) &&
                sc.getCourse().getCourseId().equals(studentCourse.getCourse().getCourseId())) {
                studentCourses.set(i, studentCourse);
                saveStudentCourses();
                return;
            }
        }
    }
    
    public List<StudentCourse> getStudentCourses(String studentId) {
        List<StudentCourse> courses = new ArrayList<>();
        for (StudentCourse sc : studentCourses) {
            if (sc.getStudentId().equals(studentId)) {
                courses.add(sc);
            }
        }
        return courses;
    }
    
    public List<StudentCourse> getStudentCoursesBySemester(String studentId, String semester, int year) {
        List<StudentCourse> courses = new ArrayList<>();
        for (StudentCourse sc : studentCourses) {
            if (sc.getStudentId().equals(studentId) &&
                sc.getSemester().equals(semester) &&
                sc.getYear() == year) {
                courses.add(sc);
            }
        }
        return courses;
    }
    
    public List<Student> getIneligibleStudents() {
        List<Student> ineligible = new ArrayList<>();
        for (Student student : students) {
            loadStudentCoursesForStudent(student);
            if (!student.isEligibleToProgress()) {
                ineligible.add(student);
            }
        }
        return ineligible;
    }
    
    public List<Student> getStudentsWithFailedCourses() {
        List<Student> failed = new ArrayList<>();
        for (Student student : students) {
            loadStudentCoursesForStudent(student);
            if (student.getFailedCoursesCount() > 0) {
                failed.add(student);
            }
        }
        return failed;
    }
    
    private void loadStudentCoursesForStudent(Student student) {
        student.getCourses().clear();
        for (StudentCourse sc : studentCourses) {
            if (sc.getStudentId().equals(student.getStudentId())) {
                student.addCourse(sc);
            }
        }
    }
    
    public double calculateCGPA(String studentId) {
        Student student = findById(studentId);
        if (student != null) {
            loadStudentCoursesForStudent(student);
            return student.calculateCGPA();
        }
        return 0.0;
    }
    
    public boolean isEligibleToProgress(String studentId) {
        Student student = findById(studentId);
        if (student != null) {
            loadStudentCoursesForStudent(student);
            return student.isEligibleToProgress();
        }
        return false;
    }
    
    public void confirmEnrolment(String studentId) {
        Student student = findById(studentId);
        if (student != null) {
            student.setEnrolled(true);
            saveStudents();
        }
    }
    
    public List<StudentCourse> getAllStudentCourses() {
        return new ArrayList<>(studentCourses);
    }
    
    public void assignGrade(String studentId, String courseId, String grade, double examScore, double assignmentScore) {
        for (StudentCourse sc : studentCourses) {
            if (sc.getStudentId().equals(studentId) && 
                sc.getCourse().getCourseId().equals(courseId)) {
                sc.setGrade(grade);
                sc.setExamScore(examScore);
                sc.setAssignmentScore(assignmentScore);
                saveStudentCourses();
                
                Student student = findById(studentId);
                if (student != null) {
                    loadStudentCoursesForStudent(student);
                    saveStudents();
                }
                return;
            }
        }
    }
    
    public void reloadData() {
        loadStudents();
        loadStudentCourses();
        for (Student student : students) {
            loadStudentCoursesForStudent(student);
        }
    }

    /**
     * Imports grades / enrolments from a delimited text file (.csv or .txt).
     *
     * Supported input formats (header row optional):
     * 1) StudentID, CourseID, Grade
     * 2) StudentID, CourseID, Grade, ExamScore, AssignmentScore
     * 3) StudentID, CourseID, Semester, Year, Grade, ExamScore, AssignmentScore [, AttemptNumber]
     *
     * Delimiters supported: comma (,), pipe (|), tab, semicolon (;)
     *
     * @return number of grade records imported
     */
    public int importGradesFromFile(String filePath, CourseService courseService) {
        int imported = 0;
        int skipped = 0;

        // Build quick lookup for courses
        Map<String, Course> courseMap = new HashMap<>();
        if (courseService != null) {
            for (Course c : courseService.getAllCourses()) {
                courseMap.put(c.getCourseId(), c);
            }
        }

        int defaultYear = java.time.LocalDate.now().getYear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Skip header if present
                String lower = line.toLowerCase();
                if (lower.startsWith("studentid") || lower.startsWith("student id")) {
                    continue;
                }

                String delimiter = detectDelimiter(line);
                String[] parts = splitLine(line, delimiter);

                if (parts.length < 3) { // need at least StudentID, CourseID, Grade
                    skipped++;
                    continue;
                }

                String studentId = parts[0].trim();
                String courseId  = parts[1].trim();

                Student student = findById(studentId);
                Course course   = courseMap.get(courseId);

                if (student == null || course == null) {
                    skipped++;
                    continue;
                }

                String semester;
                int year;
                String grade;
                double examScore = 0.0;
                double assignmentScore = 0.0;
                int attempt = 1;

                try {
                    if (parts.length >= 7) {
                        // StudentID, CourseID, Semester, Year, Grade, ExamScore, AssignmentScore [, Attempt]
                        semester = parts[2].trim();
                        year = Integer.parseInt(parts[3].trim());
                        grade = parts[4].trim();
                        examScore = Double.parseDouble(parts[5].trim());
                        assignmentScore = Double.parseDouble(parts[6].trim());
                        if (parts.length >= 8) {
                            attempt = Integer.parseInt(parts[7].trim());
                        }
                    } else if (parts.length >= 5) {
                        // StudentID, CourseID, Grade, ExamScore, AssignmentScore
                        semester = course.getSemester();
                        year = defaultYear;
                        grade = parts[2].trim();
                        examScore = Double.parseDouble(parts[3].trim());
                        assignmentScore = Double.parseDouble(parts[4].trim());
                    } else {
                        // StudentID, CourseID, Grade
                        semester = course.getSemester();
                        year = defaultYear;
                        grade = parts[2].trim();
                    }
                } catch (Exception parseEx) {
                    skipped++;
                    continue;
                }

                // Replace existing record if present (same student+course+semester+year)
                studentCourses.removeIf(sc ->
                    sc.getStudentId().equalsIgnoreCase(studentId)
                    && sc.getCourse() != null
                    && sc.getCourse().getCourseId().equalsIgnoreCase(courseId)
                    && sc.getSemester().equalsIgnoreCase(semester)
                    && sc.getYear() == year
                );

                StudentCourse sc = new StudentCourse(studentId, course, semester, year);
                sc.setAttemptNumber(attempt);
                sc.setExamScore(examScore);
                sc.setAssignmentScore(assignmentScore);
                sc.setGrade(grade); // updates status

                studentCourses.add(sc);

                imported++;
            }

            saveStudentCourses();
            reloadData();

            if (imported == 0 && skipped > 0) {
                System.err.println("Grade import: 0 imported, " + skipped + " skipped. Check file delimiter/format.");
            }

            return imported;

        } catch (IOException e) {
            throw new RuntimeException("Error importing grades: " + e.getMessage(), e);
        }
    }

}
