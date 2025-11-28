import java.util.ArrayList;
import java.util.List;

/**
 * Professor类：表示一位教授
 * 维护该教授授课的所有课程信息
 */
public class Professor {
    private String name;                                    // 教授姓名
    private List<CourseProfessor> teaching;                // 该教授授课的课程列表

    /**
     * 构造方法
     * @param name 教授姓名
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
     * 添加授课记录
     * @param courseProfessor 课程-教授关系对象
     */
    public void addTeaching(CourseProfessor courseProfessor) {
        this.teaching.add(courseProfessor);
    }

    /**
     * 计算该教授的总体平均评分
     * 遍历所有授课记录，计算所有评分的平均值
     * @return 总体平均评分
     */
    public double getOverallAverageRating() {
        if (teaching.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int totalCount = 0;

        for (Course c : courseList) {
            List<Rating> ratings = c.getRatings();
            for (Rating rating : ratings) {
                totalScore += rating.getScore();
                totalCount++;
            }
        }

        return totalCount > 0 ? totalScore / totalCount : 0.0;
    }

    @Override
    public String toString() {
        return String.format("教授: %s, 总体平均分: %.2f", name, getOverallAverageRating());
    }
}

