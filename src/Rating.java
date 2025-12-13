/**
 * Rating class: Represents a rating record
 * Contains rating score and comment content
 */
public class Rating {
    private double score;      // Rating (e.g., 0-5)
    private String comment;    // Comment content

    /**
     * Constructor
     * @param score rating score
     * @param comment comment content
     */
    public Rating(double score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    // Getter and Setter methods
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return String.format("[%.1f] %s", score, comment);
    }
}
