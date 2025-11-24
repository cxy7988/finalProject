import java.io.*;
import java.util.*;

/**
 * RatingSystem类：系统核心类
 * 负责数据管理、查询、排序等核心功能
 */
public class RatingSystem {
    private List<Course> courses;                       // 课程列表（支持遍历、排序）
    private Map<String, Course> courseMap;              // 课程映射（快速查找）
    private Map<String, Professor> professorMap;        // 教授映射（快速查找）

    /**
     * 构造方法
     */
    public RatingSystem() {
        this.courses = new ArrayList<>();
        this.courseMap = new HashMap<>();
        this.professorMap = new HashMap<>();
    }

    /**
     * 添加新课程
     * @param courseId 课程编号
     * @param courseName 课程名称
     */
    public void addCourse(String courseId, String courseName) {
        if (courseMap.containsKey(courseId)) {
            System.out.println("课程 " + courseId + " 已存在！");
            return;
        }

        Course course = new Course(courseId, courseName);
        courses.add(course);
        courseMap.put(courseId, course);
        System.out.println("成功添加课程: " + course);
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
        courses.add(course);
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
        // 获取或创建课程和教授
        Course course = getOrCreateCourse(courseId, courseName);
        Professor professor = getOrCreateProfessor(professorName);

        // 获取或创建CourseProfessor关系
        CourseProfessor cp = course.getOrCreateCourseProfessor(professor);

        // 添加评分
        Rating rating = new Rating(score, comment);
        cp.addRating(rating);

        System.out.println("成功添加评分！");
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

            System.out.println("成功从文件 " + filename + " 加载数据！");
        } catch (FileNotFoundException e) {
            System.out.println("文件未找到: " + filename);
        } catch (IOException e) {
            System.out.println("读取文件时发生错误: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("评分格式错误: " + e.getMessage());
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

            // 遍历所有课程
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

            System.out.println("成功保存数据到文件: " + filename);
        } catch (IOException e) {
            System.out.println("保存文件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 线性查找：按课程名关键字搜索
     * @param keyword 关键字
     * @return 匹配的课程列表
     */
    public List<Course> searchCoursesByName(String keyword) {
        List<Course> results = new ArrayList<>();

        for (Course course : courses) {
            if (course.getCourseName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(course);
            }
        }

        return results;
    }

    /**
     * 二分查找：按课程ID精确查找
     * 前提：courses列表必须按courseId排序
     * @param courseId 课程编号
     * @return 找到的课程，未找到返回null
     */
    public Course searchCourseById(String courseId) {
        // 先对courses按courseId排序
        courses.sort(Comparator.comparing(Course::getCourseId));

        // 二分查找
        int left = 0;
        int right = courses.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Course midCourse = courses.get(mid);
            int cmp = midCourse.getCourseId().compareTo(courseId);

            if (cmp == 0) {
                return midCourse;  // 找到
            } else if (cmp < 0) {
                left = mid + 1;    // 在右半部分
            } else {
                right = mid - 1;   // 在左半部分
            }
        }

        return null;  // 未找到
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

        // 选择排序：按平均评分从高到低
        selectionSortByRating(professorList);

        return professorList;
    }

    /**
     * 选择排序算法实现
     * 按CourseProfessor的平均评分从高到低排序
     * @param list 待排序的列表
     */
    private void selectionSortByRating(List<CourseProfessor> list) {
        int n = list.size();

        for (int i = 0; i < n - 1; i++) {
            int maxIndex = i;

            // 在[i, n-1]区间找到最大平均评分的下标
            for (int j = i + 1; j < n; j++) {
                if (list.get(j).getAverageRating() > list.get(maxIndex).getAverageRating()) {
                    maxIndex = j;
                }
            }

            // 交换
            if (maxIndex != i) {
                CourseProfessor temp = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, temp);
            }
        }
    }

    /**
     * 获取全局教授排名
     * 按总体平均评分从高到低排序
     * @return 排序后的教授列表
     */
    public List<Professor> getOverallProfessorRanking() {
        List<Professor> professorList = new ArrayList<>(professorMap.values());

        // 选择排序：按总体平均评分从高到低
        selectionSortProfessors(professorList);

        return professorList;
    }

    /**
     * 选择排序算法实现（教授版本）
     * 按Professor的总体平均评分从高到低排序
     * @param list 待排序的列表
     */
    private void selectionSortProfessors(List<Professor> list) {
        int n = list.size();

        for (int i = 0; i < n - 1; i++) {
            int maxIndex = i;

            // 在[i, n-1]区间找到最大总体平均评分的下标
            for (int j = i + 1; j < n; j++) {
                if (list.get(j).getOverallAverageRating() > list.get(maxIndex).getOverallAverageRating()) {
                    maxIndex = j;
                }
            }

            // 交换
            if (maxIndex != i) {
                Professor temp = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, temp);
            }
        }
    }

    /**
     * 显示课程详细信息
     * @param course 课程对象
     */
    public void displayCourseDetails(Course course) {
        System.out.println("\n======== 课程详细信息 ========");
        System.out.println("课程编号: " + course.getCourseId());
        System.out.println("课程名称: " + course.getCourseName());
        System.out.println("总体平均分: " + String.format("%.2f", course.getOverallAverageRating()));
        System.out.println("\n授课教授及评价：");

        List<CourseProfessor> rankedProfessors = getProfessorRankingInCourse(course.getCourseId());

        if (rankedProfessors.isEmpty()) {
            System.out.println("暂无评价数据");
            return;
        }

        int rank = 1;
        for (CourseProfessor cp : rankedProfessors) {
            System.out.println(rank + ". " + cp);
            System.out.println("   评论：");
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
        System.out.println("\n======== 教授详细信息 ========");
        System.out.println("教授姓名: " + professor.getName());
        System.out.println("总体平均分: " + String.format("%.2f", professor.getOverallAverageRating()));
        System.out.println("\n授课信息：");

        if (professor.getTeaching().isEmpty()) {
            System.out.println("暂无授课数据");
            return;
        }

        for (CourseProfessor cp : professor.getTeaching()) {
            // 需要找到对应的课程
            Course course = null;
            for (Course c : courses) {
                if (c.getProfessorList().contains(cp)) {
                    course = c;
                    break;
                }
            }

            if (course != null) {
                System.out.println("\n课程: [" + course.getCourseId() + "] " + course.getCourseName());
                System.out.println("该课程平均分: " + String.format("%.2f", cp.getAverageRating()));
                System.out.println("评论：");
                for (Rating rating : cp.getRatings()) {
                    System.out.println("  - " + rating);
                }
            }
        }
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Map<String, Professor> getProfessorMap() {
        return professorMap;
    }
}

