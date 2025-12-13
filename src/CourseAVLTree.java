import java.util.ArrayList;
import java.util.List;

/**
 * AVL tree node class
 */
class AVLNode {
    Course course;          // Stored course object
    AVLNode left;           // Left child node
    AVLNode right;          // Right child node
    int height;             // Node height

    public AVLNode(Course course) {
        this.course = course;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}

/**
 * CourseAVLTree class: Uses AVL tree to store courses
 * Sorted by course name alphabetically, supports fast lookup
 */
public class CourseAVLTree {
    private AVLNode root;

    public CourseAVLTree() {
        this.root = null;
    }

    /**
     * Get node height
     */
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Get balance factor
     */
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Update node height
     */
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    /**
     * Right rotation
     */
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        updateHeight(y);
        updateHeight(x);

        return x;
    }

    /**
     * Left rotation
     */
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    /**
     * Insert course
     * Sorted by course name in lexicographic order
     */
    public void insert(Course course) {
        root = insertNode(root, course);
    }

    private AVLNode insertNode(AVLNode node, Course course) {
        // Standard BST insertion
        if (node == null) {
            return new AVLNode(course);
        }

        int cmp = course.getCourseName().compareToIgnoreCase(node.course.getCourseName());
        
        if (cmp < 0) {
            node.left = insertNode(node.left, course);
        } else if (cmp > 0) {
            node.right = insertNode(node.right, course);
        } else {
            // Same course name, do not insert
            return node;
        }

        // Update height
        updateHeight(node);

        // Get balance factor
        int balance = getBalance(node);

        // Left-Left case
        if (balance > 1 && course.getCourseName().compareToIgnoreCase(node.left.course.getCourseName()) < 0) {
            return rotateRight(node);
        }

        // Right-Right case
        if (balance < -1 && course.getCourseName().compareToIgnoreCase(node.right.course.getCourseName()) > 0) {
            return rotateLeft(node);
        }

        // Left-Right case
        if (balance > 1 && course.getCourseName().compareToIgnoreCase(node.left.course.getCourseName()) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-Left case
        if (balance < -1 && course.getCourseName().compareToIgnoreCase(node.right.course.getCourseName()) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Search by course name keyword
     * Utilizes AVL tree ordering for optimized lookup
     */
    public List<Course> searchByName(String keyword) {
        List<Course> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        searchByNameHelper(root, lowerKeyword, results);
        return results;
    }

    private void searchByNameHelper(AVLNode node, String keyword, List<Course> results) {
        if (node == null) {
            return;
        }

        String courseName = node.course.getCourseName().toLowerCase();
        
        // If current course name contains keyword, add to results
        if (courseName.contains(keyword)) {
            results.add(node.course);
        }

        // Prune using AVL tree ordering
        // If keyword is less than current node course name, may be in left subtree
        if (keyword.compareTo(courseName) <= 0 || courseName.contains(keyword) || keyword.length() < courseName.length()) {
            searchByNameHelper(node.left, keyword, results);
        }
        
        // If keyword is greater than current node course name, may be in right subtree
        if (keyword.compareTo(courseName) >= 0 || courseName.contains(keyword) || keyword.length() < courseName.length()) {
            searchByNameHelper(node.right, keyword, results);
        }
    }

    /**
     * Search by exact course name
     */
    public Course searchByExactName(String courseName) {
        return searchByExactNameHelper(root, courseName);
    }

    private Course searchByExactNameHelper(AVLNode node, String courseName) {
        if (node == null) {
            return null;
        }

        int cmp = courseName.compareToIgnoreCase(node.course.getCourseName());
        
        if (cmp == 0) {
            return node.course;
        } else if (cmp < 0) {
            return searchByExactNameHelper(node.left, courseName);
        } else {
            return searchByExactNameHelper(node.right, courseName);
        }
    }

    /**
     * Search by course ID
     * Since AVL tree is sorted by name, this requires traversing the entire tree
     */
    public Course searchById(String courseId) {
        return searchByIdHelper(root, courseId);
    }

    private Course searchByIdHelper(AVLNode node, String courseId) {
        if (node == null) {
            return null;
        }

        if (node.course.getCourseId().equals(courseId)) {
            return node.course;
        }

        // Search left subtree first
        Course leftResult = searchByIdHelper(node.left, courseId);
        if (leftResult != null) {
            return leftResult;
        }

        // Then search right subtree
        return searchByIdHelper(node.right, courseId);
    }

    /**
     * Inorder traversal to get all courses (sorted by name)
     */
    public List<Course> getAllCoursesSorted() {
        List<Course> courses = new ArrayList<>();
        inorderTraversal(root, courses);
        return courses;
    }

    private void inorderTraversal(AVLNode node, List<Course> courses) {
        if (node != null) {
            inorderTraversal(node.left, courses);
            courses.add(node.course);
            inorderTraversal(node.right, courses);
        }
    }

    /**
     * Search all courses by first letter
     */
    public List<Course> searchByFirstLetter(char letter) {
        List<Course> results = new ArrayList<>();
        char lowerLetter = Character.toLowerCase(letter);
        searchByFirstLetterHelper(root, lowerLetter, results);
        return results;
    }

    private void searchByFirstLetterHelper(AVLNode node, char letter, List<Course> results) {
        if (node == null) {
            return;
        }

        char firstChar = Character.toLowerCase(node.course.getCourseName().charAt(0));
        
        // If first letter matches, add to results
        if (firstChar == letter) {
            results.add(node.course);
        }

        // Prune using AVL tree ordering
        if (letter <= firstChar) {
            searchByFirstLetterHelper(node.left, letter, results);
        }
        if (letter >= firstChar) {
            searchByFirstLetterHelper(node.right, letter, results);
        }
    }

    /**
     * Get number of courses in tree
     */
    public int size() {
        return sizeHelper(root);
    }

    private int sizeHelper(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + sizeHelper(node.left) + sizeHelper(node.right);
    }

    /**
     * Check if tree is empty
     */
    public boolean isEmpty() {
        return root == null;
    }
}
