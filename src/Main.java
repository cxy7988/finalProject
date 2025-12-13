import java.util.List;
import java.util.Scanner;

/**
 * Main class: Program entry point, provides command-line user interface
 */
public class Main {
    private static final String DATA_FILE = "data.csv";
    private static RatingSystem system = new RatingSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Course & Professor Rating System!");
        system.loadFromFile(DATA_FILE);

        boolean running = true;
        while (running) {
            try {
                showMenu();
                int choice = getIntInput();

                switch (choice) {
                case 1:
                    addNewRating();
                    break;
                case 2:
                    searchByCourseId();
                    break;
                case 3:
                    searchByCourseName();
                    break;
                case 4:
                    searchByProfessorName();
                    break;
                case 5:
                    showProfessorRanking();
                    break;
                case 6:
                    saveDataToFile();
                    break;
                case 7:
                    loadDataFromFile();
                    break;
                case 0:
                    running = false;
                    saveDataToFile();
                    System.out.println("Thanks for using! Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, please try again!");
            }
            } catch (Exception e) {
                System.out.println("Error occurred: " + e.getMessage());
                System.out.println("Please try again!");
            }
        }

        scanner.close();
    }

    /**
     * Display main menu
     */
    private static void showMenu() {
        System.out.println("\n===== Course & Professor Rating System =====");
        System.out.println("1. Add new rating");
        System.out.println("2. Search by course ID");
        System.out.println("3. Search by course name");
        System.out.println("4. Search by professor name");
        System.out.println("5. Show professor ranking");
        System.out.println("6. Save data");
        System.out.println("7. Reload data");
        System.out.println("0. Exit and save");
        System.out.print("Please choose an option: ");
    }

    /**
     * Get integer input
     */
    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Get double input
     */
    private static double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Reload data from data.csv
     */
    private static void loadDataFromFile() {
        System.out.println("Loading data...");
        system = new RatingSystem();
        system.loadFromFile(DATA_FILE);
    }

    /**
     * Add new rating
     */
    private static void addNewRating() {
        System.out.print("Enter course ID: ");
        String courseId = scanner.nextLine().trim();

        System.out.print("Enter course name: ");
        String courseName = scanner.nextLine().trim();

        System.out.print("Enter professor name: ");
        String professorName = scanner.nextLine().trim();

        System.out.print("Enter rating (0-5): ");
        double score = getDoubleInput();

        if (score < 0 || score > 5) {
            System.out.println("Rating must be between 0 and 5!");
            return;
        }

        System.out.print("Enter comment: ");
        String comment = scanner.nextLine().trim();

        system.addRating(courseId, courseName, professorName, score, comment);
    }

    /**
     * Search by course ID (using HashMap lookup)
     */
    private static void searchByCourseId() {
        System.out.print("Enter course ID: ");
        String courseId = scanner.nextLine().trim();

        Course course = system.searchCourseById(courseId);

        if (course == null) {
            System.out.println("Course with ID " + courseId + " not found!");
        } else {
            system.displayCourseDetails(course);
        }
    }

    /**
     * Search by course name (using AVL tree search)
     */
    private static void searchByCourseName() {
        System.out.print("Enter course name keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Course> results = system.searchCoursesByName(keyword);

        if (results.isEmpty()) {
            System.out.println("No courses found containing \"" + keyword + "\"!");
        } else {
            System.out.println("\nFound " + results.size() + " course(s):");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }

            if (results.size() == 1) {
                system.displayCourseDetails(results.get(0));
            } else {
                System.out.print("\nEnter the course number to view details (0 to skip): ");
                int choice = getIntInput();
                if (choice > 0 && choice <= results.size()) {
                    system.displayCourseDetails(results.get(choice - 1));
                }
            }
        }
    }

    /**
     * Search by professor name
     */
    private static void searchByProfessorName() {
        System.out.print("Enter professor name: ");
        String name = scanner.nextLine().trim();

        Professor professor = system.searchProfessorByName(name);

        if (professor == null) {
            System.out.println("Professor named " + name + " not found!");
        } else {
            system.displayProfessorDetails(professor);
        }
    }

    /**
     * Show professor ranking (using insertion sort)
     */
    private static void showProfessorRanking() {
        System.out.println("\nPlease choose ranking type:");
        System.out.println("1. Overall professor ranking");
        System.out.println("2. Professor ranking within a course");
        System.out.print("Please choose: ");

        int choice = getIntInput();

        if (choice == 1) {
            showOverallProfessorRanking();
        } else if (choice == 2) {
            showCourseSpecificProfessorRanking();
        } else {
            System.out.println("Invalid option!");
        }
    }

    /**
     * Show overall professor ranking
     */
    private static void showOverallProfessorRanking() {
        List<Professor> ranking = system.getOverallProfessorRanking();

        if (ranking.isEmpty()) {
            System.out.println("No professor data available!");
            return;
        }

        System.out.println("\n======== Overall Professor Ranking ========");
        System.out.print("Show top how many? (Enter 0 to show all): ");
        int topN = getIntInput();

        if (topN <= 0 || topN > ranking.size()) {
            topN = ranking.size();
        }

        for (int i = 0; i < topN; i++) {
            Professor p = ranking.get(i);
            System.out.printf("%d. %s - Average: %.2f%n",
                            i + 1,
                            p.getName(), 
                            p.getOverallAverageRating());
        }
    }

    /**
     * Show professor ranking within a specific course
     */
    private static void showCourseSpecificProfessorRanking() {
        System.out.print("Enter course ID: ");
        String courseId = scanner.nextLine().trim();

        Course course = system.searchCourseById(courseId);

        if (course == null) {
            System.out.println("Course with ID " + courseId + " not found!");
            return;
        }

        List<CourseProfessor> ranking = system.getProfessorRankingInCourse(courseId);

        if (ranking.isEmpty()) {
            System.out.println("No professor rating data for this course!");
            return;
        }

        System.out.println("\n======== Course [" + course.getCourseId() + "] " +
                         course.getCourseName() + " Professor Ranking ========");

        for (int i = 0; i < ranking.size(); i++) {
            CourseProfessor cp = ranking.get(i);
            System.out.printf("%d. %s - Average: %.2f (%d ratings)%n",
                            i + 1,
                            cp.getProfessor().getName(),
                            cp.getAverageRating(),
                            cp.getRatingCount());
        }
    }

    /**
     * Save data to data.csv
     */
    private static void saveDataToFile() {
        System.out.println("Saving data to " + DATA_FILE + "...");
        system.saveToFile(DATA_FILE);
    }
}
