import java.util.ArrayList;
import java.util.List;

/**
 * Professor class: Represents a professor
 * Maintains all course information taught by this professor
 */
public class Professor {
    private String name;                                    // Professor name
    private List<CourseProfessor> teaching;                // List of courses taught by this professor

    /**
     * Constructor
     * @param name professor name
     */
    public Professor(String name) {
        this.name = name;
        this.teaching = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CourseProfessor> getTeaching() {
        return teaching;
    }

    /**
     * Add teaching record
     * @param courseProfessor course-professor relationship object
     */
    public void addTeaching(CourseProfessor courseProfessor) {
        this.teaching.add(courseProfessor);
    }

    /**
     * Calculate overall average rating for this professor
     * Traverse all teaching records and calculate average of all ratings
     * @return overall average rating
     */
    public double getOverallAverageRating() {
        if (teaching.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int totalCount = 0;

        for (CourseProfessor cp : teaching) {
            List<Rating> ratings = cp.getRatings();
            for (Rating rating : ratings) {
                totalScore += rating.getScore();
                totalCount++;
            }
        }

        return totalCount > 0 ? totalScore / totalCount : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Professor: %s, Overall Average: %.2f", name, getOverallAverageRating());
    }
}
