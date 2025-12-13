import java.io.*;
import java.util.*;

/**
 * RatingSystem class: Core system class
 * Responsible for data management, queries, sorting and other core functions
 */
public class RatingSystem {
    private CourseAVLTree courseTree;                   // AVL tree for storing courses (sorted by name, fast lookup)
    private Map<String, Course> courseMap;              // Course map (fast lookup by ID)
    private Map<String, Professor> professorMap;        // Professor map (fast lookup)

    /**
     * Constructor
     */
    public RatingSystem() {
        this.courseTree = new CourseAVLTree();
        this.courseMap = new HashMap<>();
        this.professorMap = new HashMap<>();
    }

    /**
     * Get or create professor object
     * @param professorName professor name
     * @return Professor object
     */
    private Professor getOrCreateProfessor(String professorName) {
        if (professorMap.containsKey(professorName)) {
            return professorMap.get(professorName);
        }

        Professor professor = new Professor(professorName);
        professorMap.put(professorName, professor);
        return professor;
    }

    /**
     * Get or create course object
     * @param courseId course ID
     * @param courseName course name
     * @return Course object
     */
    private Course getOrCreateCourse(String courseId, String courseName) {
        if (courseMap.containsKey(courseId)) {
            return courseMap.get(courseId);
        }

        Course course = new Course(courseId, courseName);
        courseTree.insert(course);
        courseMap.put(courseId, course);
        return course;
    }

    /**
     * Add rating
     * @param courseId course ID
     * @param courseName course name
     * @param professorName professor name
     * @param score rating score
     * @param comment comment
     */
    public void addRating(String courseId, String courseName, String professorName, 
                         double score, String comment) {
        // Validate input
        if (courseId == null || courseId.trim().isEmpty()) {
            System.out.println("Error: Course ID cannot be empty!");
            return;
        }
        if (courseName == null || courseName.trim().isEmpty()) {
            System.out.println("Error: Course name cannot be empty!");
            return;
        }
        if (professorName == null || professorName.trim().isEmpty()) {
            System.out.println("Error: Professor name cannot be empty!");
            return;
        }
        
        // Validate rating range
        if (score < 0 || score > 5) {
            System.out.println("Error: Rating must be between 0-5! Current rating: " + score);
            return;
        }
        
        // Get or create course and professor
        Course course = getOrCreateCourse(courseId, courseName);
        Professor professor = getOrCreateProfessor(professorName);

        // Get or create CourseProfessor relationship
        CourseProfessor cp = course.getOrCreateCourseProfessor(professor);

        // Add rating
        Rating rating = new Rating(score, comment);
        cp.addRating(rating);

        System.out.println("Rating added successfully!");
    }

    /**
     * Load data from CSV file
     * Format: courseId,courseName,professorName,rating,comment
     * @param filename file name
     */
    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 5);  // Limit split to 5 parts to prevent commas in comments from affecting parsing
                if (parts.length >= 5) {
                    String courseId = parts[0].trim();
                    String courseName = parts[1].trim();
                    String professorName = parts[2].trim();
                    double score = Double.parseDouble(parts[3].trim());
                    String comment = parts[4].trim();

                    addRating(courseId, courseName, professorName, score, comment);
                }
            }

            System.out.println("Data loaded successfully from file: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Rating format error: " + e.getMessage());
        }
    }

    /**
     * Save data to CSV file
     * @param filename file name
     */
    public void saveToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            // Write header
            pw.println("courseId,courseName,professorName,rating,comment");

            // Traverse all courses (using AVL tree inorder traversal, sorted by name)
            List<Course> courses = courseTree.getAllCoursesSorted();
            for (Course course : courses) {
                for (CourseProfessor cp : course.getProfessorList()) {
                    for (Rating rating : cp.getRatings()) {
                        pw.printf("%s,%s,%s,%.1f,%s%n",
                                course.getCourseId(),
                                course.getCourseName(),
                                cp.getProfessor().getName(),
                                rating.getScore(),
                                rating.getComment());
                    }
                }
            }

            System.out.println("Data saved successfully to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * AVL tree search: Search courses by name keyword
     * Utilizes AVL tree ordering for optimized lookup
     * @param keyword keyword
     * @return list of matching courses
     */
    public List<Course> searchCoursesByName(String keyword) {
        return courseTree.searchByName(keyword);
    }

    /**
     * HashMap lookup: Search course by ID (O(1) time complexity)
     * @param courseId course ID
     * @return found course, null if not found
     */
    public Course searchCourseById(String courseId) {
        return courseMap.get(courseId);
    }

    /**
     * Search professor by name
     * @param name professor name
     * @return Professor object, null if not found
     */
    public Professor searchProfessorByName(String name) {
        return professorMap.get(name);
    }

    /**
     * Get professor ranking within a course
     * Uses insertion sort, sorted by average rating from high to low
     * @param courseId course ID
     * @return sorted list of CourseProfessor
     */
    public List<CourseProfessor> getProfessorRankingInCourse(String courseId) {
        Course course = searchCourseById(courseId);
        if (course == null) {
            return new ArrayList<>();
        }

        List<CourseProfessor> professorList = new ArrayList<>(course.getProfessorList());

        // Insertion sort: by average rating from high to low
        insertionSortByRating(professorList);

        return professorList;
    }

    /**
     * Insertion sort: Sort CourseProfessor by average rating from high to low
     */
    private void insertionSortByRating(List<CourseProfessor> list) {
        for (int i = 1; i < list.size(); i++) {

            CourseProfessor key = list.get(i);
            double keyRating = key.getAverageRating();
            int j = i - 1;

            // Move lower ratings to the right (higher ratings first)
            while (j >= 0 && list.get(j).getAverageRating() < keyRating) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }

    /**
     * Get overall professor ranking
     * Sorted by overall average rating from high to low
     * @return sorted list of professors
     */
    public List<Professor> getOverallProfessorRanking() {
        List<Professor> professorList = new ArrayList<>(professorMap.values());

        // Insertion sort: by overall average rating from high to low
        insertionSortProfessors(professorList);
        
        return professorList;
    }

    /**
     * Insertion sort algorithm implementation (Professor version)
     * Sort professors by overall average rating from high to low
     * @param list list to sort
     */
    private void insertionSortProfessors(List<Professor> list) {
        for (int i = 1; i < list.size(); i++) {

            Professor key = list.get(i);
            double keyRating = key.getOverallAverageRating();
            int j = i - 1;

            // Higher ratings first, move lower ratings to the right
            while (j >= 0 && list.get(j).getOverallAverageRating() < keyRating) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }

    /**
     * Display course details
     * @param course course object
     */
    public void displayCourseDetails(Course course) {
        System.out.println("\n======== Course Details ========");
        System.out.println("Course ID: " + course.getCourseId());
        System.out.println("Course Name: " + course.getCourseName());
        System.out.println("Overall Average Rating: " + String.format("%.2f", course.getOverallAverageRating()));
        System.out.println("\nProfessors and Ratings:");

        List<CourseProfessor> rankedProfessors = getProfessorRankingInCourse(course.getCourseId());

        if (rankedProfessors.isEmpty()) {
            System.out.println("No rating data available");
            return;
        }

        int rank = 1;
        for (CourseProfessor cp : rankedProfessors) {
            System.out.println(rank + ". " + cp);
            System.out.println("   Comments:");
            for (Rating rating : cp.getRatings()) {
                System.out.println("   - " + rating);
            }
            rank++;
        }
    }

    /**
     * Display professor details
     * @param professor professor object
     */
    public void displayProfessorDetails(Professor professor) {
        System.out.println("\n======== Professor Details ========");
        System.out.println("Professor Name: " + professor.getName());
        System.out.println("Overall Average Rating: " + String.format("%.2f", professor.getOverallAverageRating()));
        System.out.println("\nCourses Teaching:");

        if (professor.getTeaching().isEmpty()) {
            System.out.println("No course data available");
            return;
        }

        for (CourseProfessor cp : professor.getTeaching()) {
            // Find the corresponding course
            List<Course> courses = courseTree.getAllCoursesSorted();
            Course course = null;
            for (Course c : courses) {
                if (c.getProfessorList().contains(cp)) {
                    course = c;
                    break;
                }
            }

            if (course != null) {
                System.out.println("\nCourse: [" + course.getCourseId() + "] " + course.getCourseName());
                System.out.println("Course Average Rating: " + String.format("%.2f", cp.getAverageRating()));
                System.out.println("Comments:");
                for (Rating rating : cp.getRatings()) {
                    System.out.println("  - " + rating);
                }
            }
        }
    }

    /**
     * Get all courses (sorted by name)
     * @return course list
     */
    public List<Course> getCourses() {
        return courseTree.getAllCoursesSorted();
    }

    /**
     * Search courses by first letter
     * @param letter first letter
     * @return list of matching courses
     */
    public List<Course> searchCoursesByFirstLetter(char letter) {
        return courseTree.searchByFirstLetter(letter);
    }

    /**
     * Get total course count
     * @return course count
     */
    public int getCourseCount() {
        return courseTree.size();
    }

    public Map<String, Professor> getProfessorMap() {
        return professorMap;
    }
}
