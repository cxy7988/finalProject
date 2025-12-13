import java.util.ArrayList;
import java.util.List;

/**
 * CourseProfessor class (Association class)
 * ----------------------------------------------
 * Represents the relationship of "a professor teaching a course".
 *
 * In the many-to-many relationship between Course and Professor,
 * CourseProfessor serves as an "association class", responsible for recording:
 *   - Which professor (professor)
 *   - Teaching which course (course)
 *   - All ratings received for this course (ratingList)
 *
 * Each CourseProfessor instance corresponds to a
 * "Professor X teaching Course Y" relationship.
 */
public class CourseProfessor {

    /** The course object taught by this professor */
    private Course course;

    /** The professor object */
    private Professor professor;

    /** All student ratings for this professor in this course */
    private List<Rating> ratingList;

    /**
     * Constructor
     * @param course    course object
     * @param professor professor object
     *
     * Creates an association between professor and course.
     */
    public CourseProfessor(Course course, Professor professor) {
        this.course = course;
        this.professor = professor;
        this.ratingList = new ArrayList<>();
    }

    /**
     * Get associated course object
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Get associated professor object
     * @return professor
     */
    public Professor getProfessor() {
        return professor;
    }

    /**
     * Add a rating for this professor in this course
     * @param rating rating object
     */
    public void addRating(Rating rating) {
        ratingList.add(rating);
    }

    /**
     * Get all ratings
     * @return list of Rating
     */
    public List<Rating> getRatings() {
        return ratingList;
    }

    /**
     * Calculate average rating for this professor in this course
     * @return average rating (returns 0.0 if no ratings)
     */
    public double getAverageRating() {
        if (ratingList.isEmpty()) return 0.0;

        double sum = 0;
        for (Rating r : ratingList) {
            sum += r.getScore();
        }

        return sum / ratingList.size();
    }

    /**
     * Return the count of all ratings for this professor in this course
     */
    public int getRatingCount() {
        return ratingList.size();
    }

    /**
     * Return readable text description, format:
     * "Alice teaches Java Programming (Avg Rating: 4.50)"
     */
    @Override
    public String toString() {
        return professor.getName()
                + " teaches "
                + course.getCourseName()
                + " (Avg Rating: " + getAverageRating() + ")";
    }
}
