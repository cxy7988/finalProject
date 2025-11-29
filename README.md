
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

- **`Map`**

  - `HashMap<String, Course> courseMap`
  - `HashMap<String, Professor> professorMap`
  - `HashMap<String, CourseProfessor> courseProfessorMap`

  示例：
  - 键（key）：课程编号 `courseId`，例如 `"CPS1231"`
  - 值（value）：对应的 `Course` 对象

- **`List`**

  - `ArrayList<Course> courseList`
    - 存储系统中所有课程的顺序列表

  - `List<CourseProfessor>`
    - 作为 `Course` 的数据域
    - 对于每门课程，使用 `List<CourseProfessor>` 保存该课程下的所有教授及其评分信息
    - 当用户按课程查询时，从这个列表中取出所有教授并进行排序，得到“该课程下教授评分排名”

  - `List<Rating>`
    - 作为 `CourseProfessor` 的数据域
    - 保存某门课上某位教授的全部评分记录
    - 用于计算该课程-教授组合的平均分


### 3.2 选择这些数据结构的原因

- `HashMap`
    - 优点：
        - 平均查找时间复杂度为 O(1)，非常适合“按课程编号精确查找”这种高频操作
        - 键值对结构清晰，便于维护和扩展
    - 在本系统中，用于：
        - 用户输入课程 ID 时，快速定位到对应的 `Course` 对象
        - 作为“按课程查询 + 课程内教授排名”的入口

- `ArrayList<Course>`
    - 优点：
        - 支持通过下标快速访问，适合排序和遍历
        - 实现简单，是最常用的顺序容器之一
    - 在本系统中，用于：
        - 线性查找（按课程名关键字搜索）
        - 对课程按 `courseId` 进行排序，从而支持二分查找算法
        - 统计类功能（例如未来扩展“遍历所有课程的平均分”等）

- `List<CourseProfessor>`（课程内部教授列表）
    - 优点：
        - 结构紧凑，存储“某门课下的所有教授”这一小规模集合时，线性结构足够高效
        - 与排序算法结合简单，可以直接对 `List<CourseProfessor>` 进行插入排序
    - 在本系统中，用于：
        - 对某门课的所有教授按平均评分进行排序，生成“该课程下教授评分排名”

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
    - 遍历 `ArrayList<Course>` 做线性查找：时间复杂度 **O(n)**

- 对课程按编号排序以支持二分查找：
    - 假设使用简单的 `Collections.sort`（基于快速排序或 TimSort），平均时间复杂度约为 **O(n log n)**
    - 后续对课程 ID 使用二分查找：单次查找复杂度 **O(log n)**

- 在某一门课程内部，对教授按评分进行排序：
    - 对 `List<CourseProfessor>` 使用自定义选择排序：
        - 时间复杂度 **O(k²)**（k 为该课程下教授数量，一般较小）

- 计算平均分：
    - 对 `List<Rating>` 遍历一次即可：时间复杂度 **O(m)**（m 为该教授在该课程上的评分条数）



---

## 4. Algorithms（算法设计）

### 4.1 Linear Search（线性查找）

- 用途：根据**课程名关键字**（如 `Data`）模糊搜索课程
- 数据结构：`ArrayList<Course>`
- 实现思路：
    - 遍历 `courses`
    - 判断 `course.getCourseName().contains(keyword)`
- 时间复杂度：O(n)

### 4.2 Binary Search（折半查找，用于课程 ID）

- 用途：根据**课程 ID**（如 `CPS1231`）精确查找课程
- 前提条件：
    - `courses` 按 `courseId` 升序排序
- 实现思路：
    - 实现 `Course searchCourseById(String courseId)`：
        - 使用经典二分查找，对 `courseId` 比较大小
- 时间复杂度：O(log n)

### 4.3 Insertion Sort（插入排序，用于实时插入教授评分）

- **用途**：当课程内有新教授加入或对已有教授评分更新时，将这些变动实时插入排序，以保持教授评分的顺序更新。
- **数据结构**：`List<CourseProfessor>`（来自 `course.getProfessorList()`）
- **实现思路**：
    - 初始状态下假设 `professorList` 已按评分从高到低排序。
    - 将新加入或更新的教授评分插入到正确的位置：
        1. 增加或更新 `CourseProfessor` 对象到 `professorList`。
        2. 从新元素的索引开始向前比较，找到正确的插入位置。
        3. 逐个移动元素以腾出插入位置。
    - 如果是更新，可以先从原位置移除教授再重新插入。
- **时间复杂度**：最差情况下为 \(O(n²)\)，但在数据近乎有序或更新频率较低的情况下接近 \(O(n)\)。
- **在系统中的应用场景**：
    - 用户提交新教授或更新评分时 → 插入新或更新的评分到排序的 `professorList` → 保持评分顺序更新 → 输出更新后的**“该课程下教授评分排名”**

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
        - `List<Course> courses`  
          （`ArrayList<Course>`，保存所有课程，用于遍历、排序、二分查找）
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
              （线性查找：遍历 `courses`，筛选 `courseName` 包含关键字）
            - `Course searchCourseById(String courseId)`  
              （二分查找：在按 `courseId` 排好序的 `courses` 中查找）
            - `Professor searchProfessorByName(String name)`  
              （通过 `professorMap.get(name)` 直接获取）

        - 排序与排名：
            - `List<CourseProfessor> getProfessorRankingInCourse(String courseId)`
                1. 使用二分查找找到课程
                2. 拿到 `course.getProfessorList()`
                3. 调用自定义 **选择排序**，按 `CourseProfessor.getAverageRating()` 从高到低排序
                4. 返回排好序的列表，用于 UI 展示
            - `List<Professor> getOverallProfessorRanking()`（可选）  
              对所有教授按 `getOverallAverageRating()` 进行选择排序，得到全局教授排行榜

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

