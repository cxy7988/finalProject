import java.io.*;
import java.util.*;

/**
 * RatingSystem类：系统核心类
 * 负责数据管理、查询、排序等核心功能
 */
public class RatingSystem {
    private CourseAVLTree courseTree;                   // AVL树存储课程（按名称排序，快速查找）
    private Map<String, Course> courseMap;              // 课程映射（按ID快速查找）
    private Map<String, Professor> professorMap;        // 教授映射（快速查找）

    /**
     * 构造方法
     */
    public RatingSystem() {
        this.courseTree = new CourseAVLTree();
        this.courseMap = new HashMap<>();
        this.professorMap = new HashMap<>();
    }

    /**
     * 获取或创建教授对象
     * @param professorName 教授姓名
     * @return Professor对象
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
     * 获取或创建课程对象
     * @param courseId 课程编号
     * @param courseName 课程名称
     * @return Course对象
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
     * 添加评分
     * @param courseId 课程编号
     * @param courseName 课程名称
     * @param professorName 教授姓名
     * @param score 评分
     * @param comment 评论
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
        
        // 获取或创建课程和教授
        Course course = getOrCreateCourse(courseId, courseName);
        Professor professor = getOrCreateProfessor(professorName);

        // 获取或创建CourseProfessor关系
        CourseProfessor cp = course.getOrCreateCourseProfessor(professor);

        // 添加评分
        Rating rating = new Rating(score, comment);
        cp.addRating(rating);

        System.out.println("Rating added successfully!");
    }

    /**
     * 从CSV文件加载数据
     * 格式：courseId,courseName,professorName,rating,comment
     * @param filename 文件名
     */
    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // 跳过表头
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", 5);  // 限制分割为5部分，防止评论中的逗号影响
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
     * 保存数据到CSV文件
     * @param filename 文件名
     */
    public void saveToFile(String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            // 写入表头
            pw.println("courseId,courseName,professorName,rating,comment");

            // 遍历所有课程（使用AVL树的中序遍历，按名称排序）
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
     * AVL树查找：按课程名关键字搜索
     * 利用AVL树的有序性进行优化查找
     * @param keyword 关键字
     * @return 匹配的课程列表
     */
    public List<Course> searchCoursesByName(String keyword) {
        return courseTree.searchByName(keyword);
    }

    /**
     * HashMap查找：按课程ID精确查找（O(1)时间复杂度）
     * @param courseId 课程编号
     * @return 找到的课程，未找到返回null
     */
    public Course searchCourseById(String courseId) {
        return courseMap.get(courseId);
    }

    /**
     * 按教授姓名查找教授
     * @param name 教授姓名
     * @return Professor对象，未找到返回null
     */
    public Professor searchProfessorByName(String name) {
        return professorMap.get(name);
    }

    /**
     * 获取某门课程内教授的评分排名
     * 使用选择排序，按平均评分从高到低排序
     * @param courseId 课程编号
     * @return 排序后的CourseProfessor列表
     */
    public List<CourseProfessor> getProfessorRankingInCourse(String courseId) {
        Course course = searchCourseById(courseId);
        if (course == null) {
            return new ArrayList<>();
        }

        List<CourseProfessor> professorList = new ArrayList<>(course.getProfessorList());

        // 插入排序：按平均评分从高到低
        insertionSortByRating(professorList);

        return professorList;
    }

   /**
 * 插入排序：按 CourseProfessor 的平均评分从高到低排序
 */
private void insertionSortByRating(List<CourseProfessor> list) {
    for (int i = 1; i < list.size(); i++) {

        CourseProfessor key = list.get(i);
        double keyRating = key.getAverageRating();
        int j = i - 1;

        // 将评分更低的往右移动（高分在前）
        while (j >= 0 && list.get(j).getAverageRating() < keyRating) {
            list.set(j + 1, list.get(j));
            j--;
        }

        list.set(j + 1, key);
    }
}

    /**
     * 获取全局教授排名
     * 按总体平均评分从高到低排序
     * @return 排序后的教授列表
     */
    public List<Professor> getOverallProfessorRanking() {
        List<Professor> professorList = new ArrayList<>(professorMap.values());

        // 插入排序：按总体平均评分从高到低
        insertionSortProfessors(professorList);
        
        return professorList;
    }

    /**
     * 插入排序算法实现（教授版本）
     * 按Professor的总体平均评分从高到低排序
     * @param list 待排序的列表
     */
private void insertionSortProfessors(List<Professor> list) {
    for (int i = 1; i < list.size(); i++) {

        Professor key = list.get(i);
        double keyRating = key.getOverallAverageRating();
        int j = i - 1;

        // 高分在前 → 低分往右移
        while (j >= 0 && list.get(j).getOverallAverageRating() < keyRating) {
            list.set(j + 1, list.get(j));
            j--;
        }

        list.set(j + 1, key);
    }
}

    /**
     * 显示课程详细信息
     * @param course 课程对象
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
     * 显示教授详细信息
     * @param professor 教授对象
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
     * 获取所有课程（按名称排序）
     * @return 课程列表
     */
    public List<Course> getCourses() {
        return courseTree.getAllCoursesSorted();
    }

    /**
     * 按首字母查找课程
     * @param letter 首字母
     * @return 匹配的课程列表
     */
    public List<Course> searchCoursesByFirstLetter(char letter) {
        return courseTree.searchByFirstLetter(letter);
    }

    /**
     * 获取课程总数
     * @return 课程数量
     */
    public int getCourseCount() {
        return courseTree.size();
    }

    public Map<String, Professor> getProfessorMap() {
        return professorMap;
    }
}

