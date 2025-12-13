import java.util.ArrayList;
import java.util.List;

/**
 * Course class: Represents a course
 * Maintains course basic information and list of teaching professors
 */
public class Course {
    private String courseId;                            // Course ID
    private String courseName;                          // Course name
    private List<CourseProfessor> professorList;        // All professors and their ratings for this course

    /**
     * Constructor
     * @param courseId course ID
     * @param courseName course name
     */
    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.professorList = new ArrayList<>();
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<CourseProfessor> getProfessorList() {
        return professorList;
    }

    /**
     * Get or create CourseProfessor object
     * If the professor already exists in the list, return existing object; otherwise create new object and add to list
     * @param professor professor object
     * @return CourseProfessor object
     */
    public CourseProfessor getOrCreateCourseProfessor(Professor professor) {
        // Check if professor already exists
        for (CourseProfessor cp : professorList) {
            if (cp.getProfessor().getName().equals(professor.getName())) {
                return cp;
            }
        }

        // Create new if not exists
        CourseProfessor newCp = new CourseProfessor(this, professor);
        professorList.add(newCp);
        professor.addTeaching(newCp);  // Bidirectional association
        return newCp;
    }

    /**
     * Calculate overall average rating for this course
     * @return average rating
     */
    public double getOverallAverageRating() {
        if (professorList.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int totalCount = 0;

        for (CourseProfessor cp : professorList) {
            for (Rating rating : cp.getRatings()) {
                totalScore += rating.getScore();
                totalCount++;
            }
        }

        return totalCount > 0 ? totalScore / totalCount : 0.0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Average: %.2f", 
                           courseId,
                           courseName,
                           getOverallAverageRating());
    }
}
