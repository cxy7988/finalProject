import java.util.ArrayList;
import java.util.List;

/**
 * Course类：表示一门课程
 * 维护课程基本信息和授课教授列表
 */
public class Course {
    private String courseId;                            // 课程编号
    private String courseName;                          // 课程名称
    private List<CourseProfessor> professorList;        // 该课程的所有教授及其评分

    /**
     * 构造方法
     * @param courseId 课程编号
     * @param courseName 课程名称
     * @param courseIntro 课程介绍
     */
    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseIntro = courseIntro;
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
     * 获取或创建CourseProfessor对象
     * 如果该教授已经在列表中，返回现有对象；否则创建新对象并加入列表
     * @param professor 教授对象
     * @return CourseProfessor对象
     */
    public CourseProfessor getOrCreateCourseProfessor(Professor professor) {
        // 查找是否已存在该教授
        for (CourseProfessor cp : professorList) {
            if (cp.getProfessor().getName().equals(professor.getName())) {
                return cp;
            }
        }

        // 不存在则创建新的
        CourseProfessor newCp = new CourseProfessor(professor);
        professorList.add(newCp);
        professor.addTeaching(newCp);  // 双向关联
        return newCp;
    }

    /**
     * 计算该课程的总体平均评分
     * @return 平均评分
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
        return String.format("[%s] %s - 平均分: %.2f", 
                           courseId,
                           courseName,
                            courseIntro,
                           getOverallAverageRating());
    }
}

