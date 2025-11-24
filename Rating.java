package CPS2232.finalProject;

/**
 * Rating类：表示一条评分记录
 * 包含评分分数和评论内容
 */
public class Rating {
    private double score;      // 评分（例如：0-5分）
    private String comment;    // 评论内容

    /**
     * 构造方法
     * @param score 评分分数
     * @param comment 评论内容
     */
    public Rating(double score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    // Getter和Setter方法
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
        return String.format("评分: %.1f, 评论: %s", score, comment);
    }
}

