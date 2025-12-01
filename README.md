
# Course & Professor Rating System 项目文档

## 1. Introduction（简介）
- 项目背景与目标简述
- 系统名称：**Course & Professor Rating and Information Retrieval System**
- 功能概览：课程/教授信息维护、评价录入、查询与排名

---

## 2. System Overview（系统概览）
- 数据维护（Add / Update 部分）
    - 从 CSV 文件加载课程、教授、评价信息
    - 手动添加新课程 / 新教授
    - 为某课程某教授添加一条新的评价（评分 + 简短评论）
    - 保存当前数据到文件（CSV）

- 数据查询（Query 部分） 
  - 按课程代码 / 课程名查询：显示课程信息、授课教授、每个授课教授在该门课上的平均评分、所有评论
  - 按教授姓名查询：显示他教的课程、每门课的平均分、总体平均分、评论
  - 查看教授评分排名：按平均分排序并输出 —— 这里用排序算法


---

## 3. Data Structures（数据结构设计）

### 3.1 使用的数据结构类型

- **`AVL树`**（新增）

  - `CourseAVLTree courseTree`
  - AVL树是一种自平衡二叉搜索树
  - 按课程名称的字典序进行排序存储
  - 提供 O(log n) 的插入和查找时间复杂度

- **`Map`**

  - `HashMap<String, Course> courseMap`
  - `HashMap<String, Professor> professorMap`

  示例：
  - 键（key）：课程编号 `courseId`，例如 `"CPS1231"`
  - 值（value）：对应的 `Course` 对象

- **`List`**

  - `List<CourseProfessor>`
    - 作为 `Course` 的数据域
    - 对于每门课程，使用 `List<CourseProfessor>` 保存该课程下的所有教授及其评分信息
    - 当用户按课程查询时，从这个列表中取出所有教授并进行排序，得到"该课程下教授评分排名"

  - `List<Rating>`
    - 作为 `CourseProfessor` 的数据域
    - 保存某门课上某位教授的全部评分记录
    - 用于计算该课程-教授组合的平均分


### 3.2 选择这些数据结构的原因

- `CourseAVLTree`（AVL树）
    - 优点：
        - 自平衡性质保证了树的高度始终为 O(log n)
        - 插入、删除、查找操作的时间复杂度均为 O(log n)
        - 中序遍历自然得到按名称排序的课程列表
        - 相比线性查找，在数据量较大时性能优势明显
    - 在本系统中，用于：
        - 按课程名称的字典序存储所有课程
        - 快速查找包含特定关键字的课程
        - 按首字母快速筛选课程
        - 获取按名称排序的课程列表

- `HashMap`
    - 优点：
        - 平均查找时间复杂度为 O(1)，非常适合"按课程编号精确查找"这种高频操作
        - 键值对结构清晰，便于维护和扩展
    - 在本系统中，用于：
        - 用户输入课程 ID 时，快速定位到对应的 `Course` 对象
        - 作为"按课程查询 + 课程内教授排名"的入口

- `List<CourseProfessor>`（课程内部教授列表）
    - 优点：
        - 结构紧凑，存储"某门课下的所有教授"这一小规模集合时，线性结构足够高效
        - 与排序算法结合简单，可以直接对 `List<CourseProfessor>` 进行插入排序
    - 在本系统中，用于：
        - 对某门课的所有教授按平均评分进行排序，生成"该课程下教授评分排名"

- `List<Rating>`（评分列表）
    - 优点：
        - 插入新评分简单（追加到尾部）
        - 遍历计算平均值方便
    - 在本系统中，用于：
        - 存储用户对某门课上某位教授的所有评分和评论
        - 支撑平均分计算，为排序和展示提供数据基础

---

### 3.3 数据结构中的功能时间复杂度简要分析

- 按课程编号查找课程：
    - 使用 `HashMap<String, Course>`：平均时间复杂度 **O(1)**

- 按课程名关键字搜索课程：
    - 使用 AVL树查找：时间复杂度 **O(log n + k)**
    - 其中 n 是课程总数，k 是匹配结果数量
    - 利用AVL树的有序性进行剪枝优化

- AVL树相关操作：
    - 插入新课程：时间复杂度 **O(log n)**
    - 按名称精确查找：时间复杂度 **O(log n)**
    - 按首字母查找：时间复杂度 **O(log n + k)**（k为匹配数量）
    - 中序遍历获取排序列表：时间复杂度 **O(n)**
    - AVL树自平衡操作（旋转）：时间复杂度 **O(1)**

- 在某一门课程内部，对教授按评分进行排序：
    - 对 `List<CourseProfessor>` 使用自定义插入排序：
        - 时间复杂度 **O(k²)**（k 为该课程下教授数量，一般较小）

- 计算平均分：
    - 对 `List<Rating>` 遍历一次即可：时间复杂度 **O(m)**（m 为该教授在该课程上的评分条数）



---

## 4. Algorithms（算法设计）

### 4.1 AVL树算法（用于课程存储和查找）

#### 4.1.1 AVL树基本原理
- AVL树是一种自平衡二叉搜索树
- 任何节点的左右子树高度差不超过1
- 通过旋转操作维持平衡性

#### 4.1.2 AVL树的旋转操作
- **左旋（Left Rotation）**：当右子树过高时使用
- **右旋（Right Rotation）**：当左子树过高时使用
- **左右旋（Left-Right Rotation）**：先左旋后右旋
- **右左旋（Right-Left Rotation）**：先右旋后左旋

#### 4.1.3 课程查找算法
- **按课程名关键字搜索**：
    - 利用AVL树的有序性进行优化查找
    - 通过比较关键字与节点值进行剪枝
    - 时间复杂度：O(log n + k)，k为匹配数量
    
- **按首字母查找**：
    - 利用课程名称的字典序特性
    - 在AVL树中快速定位首字母范围
    - 时间复杂度：O(log n + k)

- **按课程ID查找**：
    - 使用 `HashMap<String, Course>`
    - 时间复杂度：O(1)

### 4.2 Hash查找（用于课程ID和教授名称）

- 用途：根据**课程ID**或**教授名称**精确查找
- 数据结构：`HashMap<String, Course>` 和 `HashMap<String, Professor>`
- 实现思路：
    - 使用HashMap的get方法直接获取对象
- 时间复杂度：O(1)

### 4.3 Insertion Sort（插入排序，用于教授评分排序）

- **用途**：对课程内的教授按评分进行排序
- **数据结构**：`List<CourseProfessor>`（来自 `course.getProfessorList()`）
- **实现思路**：
    - 从第二个元素开始，依次插入到前面已排序的序列中
    - 每次插入时，从后向前比较，找到合适的位置
    - 将评分较高的教授排在前面
- **时间复杂度**：
    - 最坏情况：O(n²)
    - 最好情况：O(n)（数据已基本有序）
    - 平均情况：O(n²)
- **在系统中的应用场景**：
    - 显示某门课程内教授的评分排名
    - 由于每门课的教授数量通常较小，插入排序的性能表现良好


## 5. Class Design（类设计概述）

### 5.1 核心类说明

- `class Rating`
    - 字段：
        - `double score`（评分）
        - `String comment`（评论）
    - 方法：
        - 构造方法
        - getter / setter

- `class Professor`
    - 字段：
        - `String name`
        - `List<CourseProfessor> teaching`  
          （这个教授在哪些课程中授课）
    - 方法：
        - `double getOverallAverageRating()`  
          （遍历 `teaching` 中所有 `CourseProfessor` 的评分，计算总体平均分）

- `class CourseProfessor`
    - 含义：表示“某门课上的某个教授”，是课程与教授的中间关系类
    - 字段：
        - `Professor professor`
        - `List<Rating> ratings`  （对“这门课上的这个教授”的所有评价）
    - 方法：
        - `double getAverageRating()`  
          （计算当前课程-教授组合的平均评分）

- `class Course`
    - 字段：
        - `String courseId`
        - `String courseName`
        - `List<CourseProfessor> professorList`  
          （这门课下所有教授及对应的评分）
    - 方法：
        - `CourseProfessor getOrCreateCourseProfessor(Professor p)`  
          （若列表中已存在该教授则返回，否则新建一个 `CourseProfessor` 加入列表）
        - `List<CourseProfessor> getProfessorList()`  
          （用于后续排序和展示）

- `class RatingSystem`
    - 字段：
        - `CourseAVLTree courseTree`  
          （AVL树，按课程名称字典序存储所有课程，支持快速查找）
        - `Map<String, Course> courseMap`  
          （`HashMap<String, Course>`，按课程ID快速查找课程）
        - `Map<String, Professor> professorMap`  
          （`HashMap<String, Professor>`，按教授姓名快速查找教授）
    - 主要方法（部分）：
        - 数据维护：
            - `void addCourse(String id, String name)`
            - `void addRating(String courseId, String courseName, String professorName, double score, String comment)`
        - 文件读写：
            - `void loadFromFile(String filename)`
            - `void saveToFile(String filename)`
        - 查询（与算法对应）：
            - `List<Course> searchCoursesByName(String keyword)`  
              （AVL树查找：利用树的有序性进行优化查找）
            - `Course searchCourseById(String courseId)`  
              （HashMap查找：O(1)时间复杂度直接获取）
            - `Professor searchProfessorByName(String name)`  
              （通过 `professorMap.get(name)` 直接获取）
            - `List<Course> searchCoursesByFirstLetter(char letter)`  
              （按首字母查找：利用AVL树的有序性快速定位）

        - 排序与排名：
            - `List<CourseProfessor> getProfessorRankingInCourse(String courseId)`
                1. 使用HashMap找到课程（O(1)）
                2. 拿到 `course.getProfessorList()`
                3. 调用自定义**插入排序**，按 `CourseProfessor.getAverageRating()` 从高到低排序
                4. 返回排好序的列表，用于 UI 展示
            - `List<Professor> getOverallProfessorRanking()`  
              对所有教授按 `getOverallAverageRating()` 进行插入排序，得到全局教授排行榜
            - `List<Course> getCourses()`  
              返回按名称排序的所有课程列表（AVL树中序遍历）

- `class CourseAVLTree`（新增）
    - 字段：
        - `AVLNode root`（树的根节点）
    - 主要方法：
        - `void insert(Course course)`  
          插入课程，自动维持AVL树平衡
        - `List<Course> searchByName(String keyword)`  
          按关键字搜索课程
        - `Course searchByExactName(String courseName)`  
          精确名称查找
        - `Course searchById(String courseId)`  
          按ID查找（需遍历整棵树）
        - `List<Course> searchByFirstLetter(char letter)`  
          按首字母查找
        - `List<Course> getAllCoursesSorted()`  
          中序遍历返回排序列表
        - `int size()`  
          返回树中课程数量

- `class AVLNode`（新增）
    - 字段：
        - `Course course`（存储的课程对象）
        - `AVLNode left`（左子节点）
        - `AVLNode right`（右子节点）
        - `int height`（节点高度）

---

## 6. User Interface（用户界面）

### 6.1 交互方式
- 使用命令行菜单（Command-Line Interface）

### 6.2 菜单结构示例

    ===== Course & Professor Rating System =====
    1. Load data from file
    2. Add new course
    3. Add new rating
    4. Search by course ID
    5. Search by course name (keyword)
    6. Search by professor name
    7. Show professor ranking
    8. Save data to file
    0. Exit
    Please choose an option:

### 6.3 示例交互流程

- 用户选择菜单项
- 输入查询关键字或评分信息
- 程序输出对应课程/教授信息与评价

---

## 7. Input / Output（文件读写）

### 7.1 输入文件格式（CSV 示例）

    courseId,courseName,professorName,rating,comment
    CPS1231,Java Programming,Dr. Smith,4.5,Very clear explanation
    MATH2413,Calculus I,Dr. Lee,3.8,Homework is heavy
    ...

### 7.2 文件读取流程

- 使用 `BufferedReader` 逐行读取
- 使用 `split(",")` 解析字段
- 根据 `courseId`、`courseName`、`professorName` 创建或获取对象
- 将 `Rating` 加入对应 `Course` 和 `Professor`

### 7.3 文件保存流程

- 遍历所有课程 `courses`
- 对每条 `Rating` 写一行 CSV
- 输出文件如 `output.csv`


### 代码更新日志

#### 1.0.2 (最新版本)
- **重大更新**：使用AVL树替代ArrayList存储课程
    - 新增 `CourseAVLTree` 类和 `AVLNode` 类
    - 实现AVL树的插入、平衡和旋转操作
    - 支持按课程名称字典序自动排序
    - 查找性能从 O(n) 提升到 O(log n)
- **优化**：改进课程查找算法
    - 按名称关键字搜索：利用AVL树有序性进行剪枝优化
    - 新增按首字母查找功能
    - 课程ID查找改用HashMap，时间复杂度为O(1)
- **改进**：文件保存时自动按课程名称排序输出

#### 1.0.1
- 修复Professor类中getOverallAverageRating()方法的实现，使用teaching列表而不是courseList列表
- getOrCreateCourseProfessor方法不应该接收course参数，因为它已经是Course类的成员方法
- RatingSystem.java缺少判定：addcourse和addrating可能存在输入评分大于5的情况，已添加判断
