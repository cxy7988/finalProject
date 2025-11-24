import java.util.List;
import java.util.Scanner;

/**
 * Main类：程序入口，提供命令行用户界面
 */
public class Main {
    private static final String DATA_FILE = "data.csv";
    private static RatingSystem system = new RatingSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("欢迎使用课程与教授评分系统！");
        System.out.println("Welcome to Course & Professor Rating System!");
        System.out.println("\n正在从 " + DATA_FILE + " 加载数据...");
        system.loadFromFile(DATA_FILE);

        boolean running = true;
        while (running) {
            showMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1:
                    addNewCourse();
                    break;
                case 2:
                    addNewRating();
                    break;
                case 3:
                    searchByCourseId();
                    break;
                case 4:
                    searchByCourseName();
                    break;
                case 5:
                    searchByProfessorName();
                    break;
                case 6:
                    showProfessorRanking();
                    break;
                case 7:
                    saveDataToFile();
                    break;
                case 8:
                    loadDataFromFile();
                    break;
                case 0:
                    running = false;
                    saveDataToFile();
                    System.out.println("感谢使用，再见！");
                    break;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
        }

        scanner.close();
    }

    /**
     * 显示主菜单
     */
    private static void showMenu() {
        System.out.println("\n===== Course & Professor Rating System =====");
        System.out.println("1. 添加新课程 (Add new course)");
        System.out.println("2. 添加新评价 (Add new rating)");
        System.out.println("3. 按课程ID查询 (Search by course ID)");
        System.out.println("4. 按课程名查询 (Search by course name)");
        System.out.println("5. 按教授姓名查询 (Search by professor name)");
        System.out.println("6. 显示教授排名 (Show professor ranking)");
        System.out.println("7. 保存数据 (Save data)");
        System.out.println("8. 重新加载数据 (Reload data)");
        System.out.println("0. 退出并保存 (Exit and save)");
        System.out.print("请选择操作 (Please choose an option): ");
    }

    /**
     * 获取整数输入
     */
    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取浮点数输入
     */
    private static double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 从data.csv重新加载数据
     */
    private static void loadDataFromFile() {
        System.out.println("正在从 " + DATA_FILE + " 重新加载数据...");
        system = new RatingSystem();
        system.loadFromFile(DATA_FILE);
    }

    /**
     * 添加新课程
     */
    private static void addNewCourse() {
        System.out.print("请输入课程编号 (例如: CPS1231): ");
        String courseId = scanner.nextLine().trim();

        System.out.print("请输入课程名称 (例如: Java Programming): ");
        String courseName = scanner.nextLine().trim();

        system.addCourse(courseId, courseName);
    }

    /**
     * 添加新评价
     */
    private static void addNewRating() {
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine().trim();

        System.out.print("请输入课程名称: ");
        String courseName = scanner.nextLine().trim();

        System.out.print("请输入教授姓名: ");
        String professorName = scanner.nextLine().trim();

        System.out.print("请输入评分 (0-5): ");
        double score = getDoubleInput();

        if (score < 0 || score > 5) {
            System.out.println("评分必须在0-5之间！");
            return;
        }

        System.out.print("请输入评论: ");
        String comment = scanner.nextLine().trim();

        system.addRating(courseId, courseName, professorName, score, comment);
    }

    /**
     * 按课程ID查询（使用二分查找）
     */
    private static void searchByCourseId() {
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine().trim();

        Course course = system.searchCourseById(courseId);

        if (course == null) {
            System.out.println("未找到课程编号为 " + courseId + " 的课程！");
        } else {
            system.displayCourseDetails(course);
        }
    }

    /**
     * 按课程名查询（使用线性查找）
     */
    private static void searchByCourseName() {
        System.out.print("请输入课程名关键字: ");
        String keyword = scanner.nextLine().trim();

        List<Course> results = system.searchCoursesByName(keyword);

        if (results.isEmpty()) {
            System.out.println("未找到包含 \"" + keyword + "\" 的课程！");
        } else {
            System.out.println("\n找到 " + results.size() + " 门课程：");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }

            if (results.size() == 1) {
                system.displayCourseDetails(results.get(0));
            } else {
                System.out.print("\n请输入要查看详情的课程序号 (输入0跳过): ");
                int choice = getIntInput();
                if (choice > 0 && choice <= results.size()) {
                    system.displayCourseDetails(results.get(choice - 1));
                }
            }
        }
    }

    /**
     * 按教授姓名查询
     */
    private static void searchByProfessorName() {
        System.out.print("请输入教授姓名: ");
        String name = scanner.nextLine().trim();

        Professor professor = system.searchProfessorByName(name);

        if (professor == null) {
            System.out.println("未找到姓名为 " + name + " 的教授！");
        } else {
            system.displayProfessorDetails(professor);
        }
    }

    /**
     * 显示教授排名（使用选择排序）
     */
    private static void showProfessorRanking() {
        System.out.println("\n请选择排名方式：");
        System.out.println("1. 全局教授排名");
        System.out.println("2. 某门课程内教授排名");
        System.out.print("请选择: ");

        int choice = getIntInput();

        if (choice == 1) {
            showOverallProfessorRanking();
        } else if (choice == 2) {
            showCourseSpecificProfessorRanking();
        } else {
            System.out.println("无效选项！");
        }
    }

    /**
     * 显示全局教授排名
     */
    private static void showOverallProfessorRanking() {
        List<Professor> ranking = system.getOverallProfessorRanking();

        if (ranking.isEmpty()) {
            System.out.println("暂无教授数据！");
            return;
        }

        System.out.println("\n======== 全局教授排名 ========");
        System.out.print("显示前几名？(输入0显示全部): ");
        int topN = getIntInput();

        if (topN <= 0 || topN > ranking.size()) {
            topN = ranking.size();
        }

        for (int i = 0; i < topN; i++) {
            Professor p = ranking.get(i);
            System.out.printf("%d. %s - 平均分: %.2f%n", 
                            i + 1, 
                            p.getName(), 
                            p.getOverallAverageRating());
        }
    }

    /**
     * 显示某门课程内教授排名
     */
    private static void showCourseSpecificProfessorRanking() {
        System.out.print("请输入课程编号: ");
        String courseId = scanner.nextLine().trim();

        Course course = system.searchCourseById(courseId);

        if (course == null) {
            System.out.println("未找到课程编号为 " + courseId + " 的课程！");
            return;
        }

        List<CourseProfessor> ranking = system.getProfessorRankingInCourse(courseId);

        if (ranking.isEmpty()) {
            System.out.println("该课程暂无教授评价数据！");
            return;
        }

        System.out.println("\n======== 课程 [" + course.getCourseId() + "] " + 
                         course.getCourseName() + " 教授排名 ========");

        for (int i = 0; i < ranking.size(); i++) {
            CourseProfessor cp = ranking.get(i);
            System.out.printf("%d. %s - 平均分: %.2f (%d条评价)%n",
                            i + 1,
                            cp.getProfessor().getName(),
                            cp.getAverageRating(),
                            cp.getRatingCount());
        }
    }

    /**
     * 保存数据到data.csv
     */
    private static void saveDataToFile() {
        System.out.println("正在保存数据到 " + DATA_FILE + "...");
        system.saveToFile(DATA_FILE);
    }
}

