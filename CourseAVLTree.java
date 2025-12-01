import java.util.ArrayList;
import java.util.List;

/**
 * AVL树节点类
 */
class AVLNode {
    Course course;          // 存储的课程对象
    AVLNode left;           // 左子节点
    AVLNode right;          // 右子节点
    int height;             // 节点高度

    public AVLNode(Course course) {
        this.course = course;
        this.left = null;
        this.right = null;
        this.height = 1;
    }
}

/**
 * CourseAVLTree类：使用AVL树存储课程
 * 按照课程名称的首字母进行排序，支持快速查找
 */
public class CourseAVLTree {
    private AVLNode root;

    public CourseAVLTree() {
        this.root = null;
    }

    /**
     * 获取节点高度
     */
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    /**
     * 获取平衡因子
     */
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * 更新节点高度
     */
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    /**
     * 右旋转
     */
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // 执行旋转
        x.right = y;
        y.left = T2;

        // 更新高度
        updateHeight(y);
        updateHeight(x);

        return x;
    }

    /**
     * 左旋转
     */
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // 执行旋转
        y.left = x;
        x.right = T2;

        // 更新高度
        updateHeight(x);
        updateHeight(y);

        return y;
    }

    /**
     * 插入课程
     * 按课程名称的字典序排序
     */
    public void insert(Course course) {
        root = insertNode(root, course);
    }

    private AVLNode insertNode(AVLNode node, Course course) {
        // 标准BST插入
        if (node == null) {
            return new AVLNode(course);
        }

        int cmp = course.getCourseName().compareToIgnoreCase(node.course.getCourseName());
        
        if (cmp < 0) {
            node.left = insertNode(node.left, course);
        } else if (cmp > 0) {
            node.right = insertNode(node.right, course);
        } else {
            // 课程名称相同，不插入
            return node;
        }

        // 更新高度
        updateHeight(node);

        // 获取平衡因子
        int balance = getBalance(node);

        // 左左情况
        if (balance > 1 && course.getCourseName().compareToIgnoreCase(node.left.course.getCourseName()) < 0) {
            return rotateRight(node);
        }

        // 右右情况
        if (balance < -1 && course.getCourseName().compareToIgnoreCase(node.right.course.getCourseName()) > 0) {
            return rotateLeft(node);
        }

        // 左右情况
        if (balance > 1 && course.getCourseName().compareToIgnoreCase(node.left.course.getCourseName()) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // 右左情况
        if (balance < -1 && course.getCourseName().compareToIgnoreCase(node.right.course.getCourseName()) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * 按课程名称关键字搜索
     * 利用AVL树的有序性进行优化查找
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
        
        // 如果当前课程名包含关键字，加入结果
        if (courseName.contains(keyword)) {
            results.add(node.course);
        }

        // 利用AVL树的有序性进行剪枝
        // 如果关键字小于当前节点课程名，可能在左子树
        if (keyword.compareTo(courseName) <= 0 || courseName.contains(keyword) || keyword.length() < courseName.length()) {
            searchByNameHelper(node.left, keyword, results);
        }
        
        // 如果关键字大于当前节点课程名，可能在右子树
        if (keyword.compareTo(courseName) >= 0 || courseName.contains(keyword) || keyword.length() < courseName.length()) {
            searchByNameHelper(node.right, keyword, results);
        }
    }

    /**
     * 按课程名称精确查找
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
     * 按课程ID查找
     * 由于AVL树是按名称排序的，这里需要遍历整个树
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

        // 先查左子树
        Course leftResult = searchByIdHelper(node.left, courseId);
        if (leftResult != null) {
            return leftResult;
        }

        // 再查右子树
        return searchByIdHelper(node.right, courseId);
    }

    /**
     * 中序遍历获取所有课程（按名称排序）
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
     * 按首字母查找所有课程
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
        
        // 如果首字母匹配，加入结果
        if (firstChar == letter) {
            results.add(node.course);
        }

        // 利用AVL树的有序性进行剪枝
        if (letter <= firstChar) {
            searchByFirstLetterHelper(node.left, letter, results);
        }
        if (letter >= firstChar) {
            searchByFirstLetterHelper(node.right, letter, results);
        }
    }

    /**
     * 获取树中课程数量
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
     * 检查树是否为空
     */
    public boolean isEmpty() {
        return root == null;
    }
}

