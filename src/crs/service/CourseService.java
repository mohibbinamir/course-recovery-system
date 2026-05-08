package crs.service;

import crs.model.Course;
import crs.util.FileManager;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class CourseService {
    
    private static final String COURSES_FILE = "courses.dat";
    
    private List<Course> courses;
    
    private static CourseService instance;
    
    private CourseService() {
        loadCourses();
    }
    
    public static CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService();
        }
        return instance;
    }
    
    private void loadCourses() {
        courses = FileManager.loadFromBinaryFile(COURSES_FILE);
        if (courses == null) {
            courses = new ArrayList<>();
        }
    }
    
    public void saveCourses() {
        FileManager.saveToBinaryFile(COURSES_FILE, courses);
    }
    
    /**
 * Imports courses from a delimited text file (.csv or .txt).
 * Supported delimiters: comma (,), pipe (|), tab (	), semicolon (;)
 * Header row is optional.
 *
 * @return number of newly imported courses (duplicates are ignored)
 */
public int importCoursesFromFile(String filePath) {
    int imported = 0;
    int skipped = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Skip header if present
            String lower = line.toLowerCase();
            if (lower.startsWith("courseid") || lower.startsWith("course id")) {
                continue;
            }

            String delimiter = detectDelimiter(line);
            String[] parts = splitLine(line, delimiter);

            if (parts.length < 7) {
                skipped++;
                continue;
            }

            try {
                Course course = new Course(
                    parts[0].trim(),
                    parts[1].trim(),
                    parseIntFlexible(parts[2]),
                    parts[3].trim(),
                    parts[4].trim(),
                    parseIntFlexible(parts[5]),
                    parseIntFlexible(parts[6])
                );

                if (findById(course.getCourseId()) == null) {
                    courses.add(course);
                    imported++;
                }
            } catch (Exception ex) {
                skipped++;
            }
        }

        saveCourses();

        if (imported == 0 && skipped > 0) {
            System.err.println("Course import: 0 imported, " + skipped + " skipped. Check file delimiter/format.");
        }

        return imported;

    } catch (IOException e) {
        throw new RuntimeException("Error importing courses: " + e.getMessage(), e);
    }
}

// Backward-compatible name (UI calls this)
public void importCoursesFromCSV(String filePath) {
    importCoursesFromFile(filePath);
}

private String detectDelimiter(String line) {
    // Pick the delimiter that appears most often in the line
    String[] candidates = {",", "|", "	", ";"};
    int bestCount = 0;
    String best = ","; // default

    for (String c : candidates) {
        int count = countOccurrences(line, c.equals("	") ? "	" : c);
        if (count > bestCount) {
            bestCount = count;
            best = c;
        }
    }

    // If no delimiter found, keep default comma
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

private int parseIntFlexible(String raw) {
    if (raw == null) throw new NumberFormatException("null");
    // Allow values like "60%", " 60 ", etc.
    String cleaned = raw.trim().replaceAll("[^0-9-]", "");
    if (cleaned.isEmpty()) throw new NumberFormatException("Invalid number: " + raw);
    return Integer.parseInt(cleaned);
}
    
    public Course addCourse(Course course) {
        if (findById(course.getCourseId()) != null) {
            throw new IllegalArgumentException("Course ID already exists");
        }
        courses.add(course);
        saveCourses();
        return course;
    }
    
    public void updateCourse(Course course) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equals(course.getCourseId())) {
                courses.set(i, course);
                saveCourses();
                return;
            }
        }
    }
    
    public void deleteCourse(String courseId) {
        courses.removeIf(c -> c.getCourseId().equals(courseId));
        saveCourses();
    }
    
    public Course findById(String courseId) {
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }
    
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    
    public List<Course> searchCourses(String keyword) {
        List<Course> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Course course : courses) {
            if (course.getCourseId().toLowerCase().contains(lower) ||
                course.getCourseName().toLowerCase().contains(lower) ||
                course.getInstructor().toLowerCase().contains(lower)) {
                results.add(course);
            }
        }
        return results;
    }
    
    public List<Course> getCoursesBySemester(String semester) {
        List<Course> results = new ArrayList<>();
        for (Course course : courses) {
            if (course.getSemester().equalsIgnoreCase(semester)) {
                results.add(course);
            }
        }
        return results;
    }
    
    public List<Course> getCoursesByInstructor(String instructor) {
        List<Course> results = new ArrayList<>();
        for (Course course : courses) {
            if (course.getInstructor().equalsIgnoreCase(instructor)) {
                results.add(course);
            }
        }
        return results;
    }
    
    public List<String> getAllSemesters() {
        Set<String> semesters = new HashSet<>();
        for (Course course : courses) {
            semesters.add(course.getSemester());
        }
        return new ArrayList<>(semesters);
    }
    
    public List<String> getAllInstructors() {
        Set<String> instructors = new HashSet<>();
        for (Course course : courses) {
            instructors.add(course.getInstructor());
        }
        return new ArrayList<>(instructors);
    }
}
