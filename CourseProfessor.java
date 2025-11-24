import java.util.ArrayList;
import java.util.List;

/**
 * CourseProfessor类：表示"某门课上的某位教授"
 * 这是课程与教授的中间关系类，包含该组合的所有评分
 */
public class CourseProfessor {
    private Professor professor;        // 教授对象
    private List<Rating> ratings;       // 该课程-教授组合的所有评分

    /**
     * 构造方法
     * @param professor 教授对象
     */
    public CourseProfessor(Professor professor) {
        this.professor = professor;
        this.ratings = new ArrayList<>();
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    /**
     * 添加一条评分记录
     * @param rating 评分对象
     */
    public void addRating(Rating rating) {
        this.ratings.add(rating);
    }

    /**
     * 计算该课程-教授组合的平均评分
     * @return 平均评分
     */
    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Rating rating : ratings) {
            sum += rating.getScore();
        }

        return sum / ratings.size();
    }

    /**
     * 获取评分数量
     * @return 评分总数
     */
    public int getRatingCount() {
        return ratings.size();
    }

    @Override
    public String toString() {
        return String.format("%s - 平均分: %.2f (%d条评价)", 
                           professor.getName(), 
                           getAverageRating(), 
                           getRatingCount());
    }
}

