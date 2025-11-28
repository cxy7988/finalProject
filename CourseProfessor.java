import java.util.ArrayList;
import java.util.List;

/**
 * CourseProfessor 类（关联类）
 * ----------------------------------------------
 * 用于表示「某位教授教授某门课程」这一关系。
 *
 * 在 Course 与 Professor 的多对多关系中，
 * CourseProfessor 作为“关联类”，负责记录：
 *   - 哪位教授（professor）
 *   - 在教授哪门课程（course）
 *   - 在该课程上收到的所有评分（ratingList）
 *
 * 每个 CourseProfessor 实例都对应一种
 * “教授 X 在课程 Y 上的授课关系”。
 */
public class CourseProfessor {

    /** 该教授所教授的课程对象 */
    private Course course;

    /** 授课的教授对象 */
    private Professor professor;

    /** 学生对该教授在该课程上的所有评分 */
    private List<Rating> ratingList;

    /**
     * 构造方法
     * @param course    课程对象
     * @param professor 教授对象
     *
     * 创建一个教授与课程的关联关系。
     */
    public CourseProfessor(Course course, Professor professor) {
        this.course = course;
        this.professor = professor;
        this.ratingList = new ArrayList<>();
    }

    /**
     * 获取关联的课程对象
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * 获取关联的教授对象
     * @return professor
     */
    public Professor getProfessor() {
        return professor;
    }

    /**
     * 为该教授在该课程上的授课添加一条评分
     * @param rating 评分对象
     */
    public void addRating(Rating rating) {
        ratingList.add(rating);
    }

    /**
     * 获取所有评分
     * @return Rating 列表
     */
    public List<Rating> getRatings() {
        return ratingList;
    }

    /**
     * 计算该教授在该课程上的平均评分
     * @return 平均分（若无评价则返回 0.0）
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
     * 返回当前教授在本课程下的所有评价数量
     */
    public int getRatingCount() {
        // ratingList 是存储所有评分的列表
        // 如果没有评分，ratingList 可能为 empty，但不为 null（你应该在构造方法里初始化）
        return ratingList.size();
    }

    /**
     * 返回易读的文本描述，格式如：
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
