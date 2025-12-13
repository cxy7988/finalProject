# Course & Professor Rating System 测试文档

本文档包含系统各功能的测试用例，包括输入和预期输出。

---

## 测试环境

- 测试数据文件：`data.csv`
- 初始数据包含：5门课程，7位教授，19条评分记录

---

## 测试用例

### 1. 添加新评分 (Add new rating)

#### 测试 1.1：为已存在的课程和教授添加评分

**输入：**
```
Please choose an option: 1
Enter course ID: CPS1231
Enter course name: Java Programming
Enter professor name: Dr. Smith
Enter rating (0-5): 4.7
Enter comment: Great lecture today
```

**预期输出：**
```
Rating added successfully!
```

#### 测试 1.2：为新课程添加评分（自动创建课程和教授）

**输入：**
```
Please choose an option: 1
Enter course ID: CS101
Enter course name: Introduction to Computer Science
Enter professor name: Dr. Wilson
Enter rating (0-5): 4.5
Enter comment: Excellent introduction course
```

**预期输出：**
```
Rating added successfully!
```

#### 测试 1.3：评分超出范围（错误处理）

**输入：**
```
Please choose an option: 1
Enter course ID: CPS1231
Enter course name: Java Programming
Enter professor name: Dr. Smith
Enter rating (0-5): 6.0
```

**预期输出：**
```
Rating must be between 0 and 5!
```

#### 测试 1.4：评分为负数（错误处理）

**输入：**
```
Please choose an option: 1
Enter course ID: CPS1231
Enter course name: Java Programming
Enter professor name: Dr. Smith
Enter rating (0-5): -1
```

**预期输出：**
```
Rating must be between 0 and 5!
```

---

### 2. 按课程ID查询 (Search by course ID)

#### 测试 2.1：查询存在的课程

**输入：**
```
Please choose an option: 2
Enter course ID: CPS1231
```

**预期输出：**
```
======== Course Details ========
Course ID: CPS1231
Course Name: Java Programming
Overall Average Rating: 4.20

Professors and Ratings:
1. Dr. Smith teaches Java Programming (Avg Rating: 4.65)
   Comments:
   - [4.5] Very clear explanation
   - [4.8] Excellent teaching style
2. Dr. Lee teaches Java Programming (Avg Rating: 4.2)
   Comments:
   - [4.2] Very helpful during office hours
3. Dr. Johnson teaches Java Programming (Avg Rating: 3.75)
   Comments:
   - [3.5] Assignments are difficult
   - [4.0] Good course materials
```

#### 测试 2.2：查询不存在的课程

**输入：**
```
Please choose an option: 2
Enter course ID: INVALID999
```

**预期输出：**
```
Course with ID INVALID999 not found!
```

---

### 3. 按课程名查询 (Search by course name)

#### 测试 3.1：关键字匹配单个课程

**输入：**
```
Please choose an option: 3
Enter course name keyword: Java
```

**预期输出：**
```
Found 1 course(s):
1. [CPS1231] Java Programming - Average: 4.20

======== Course Details ========
Course ID: CPS1231
Course Name: Java Programming
Overall Average Rating: 4.20

Professors and Ratings:
...(details)
```

#### 测试 3.2：关键字匹配多个课程

**输入：**
```
Please choose an option: 3
Enter course name keyword: i
```

**预期输出：**
```
Found 4 course(s):
1. [MATH2413] Calculus I - Average: 4.25
2. [ENG1301] English Composition - Average: 4.05
3. [CPS1231] Java Programming - Average: 4.20
4. [PHYS2325] University Physics - Average: 4.15

Enter the course number to view details (0 to skip): 
```

**输入（继续）：**
```
2
```

**预期输出：**
```
======== Course Details ========
Course ID: ENG1301
Course Name: English Composition
Overall Average Rating: 4.05

Professors and Ratings:
1. Dr. Davis teaches English Composition (Avg Rating: 4.05)
   Comments:
   - [3.9] Helpful feedback on essays
   - [4.2] Engaging discussions
```

#### 测试 3.3：关键字无匹配结果

**输入：**
```
Please choose an option: 3
Enter course name keyword: Chemistry
```

**预期输出：**
```
No courses found containing "Chemistry"!
```

---

### 4. 按教授姓名查询 (Search by professor name)

#### 测试 4.1：查询存在的教授

**输入：**
```
Please choose an option: 4
Enter professor name: Dr. Smith
```

**预期输出：**
```
======== Professor Details ========
Professor Name: Dr. Smith
Overall Average Rating: 4.70

Courses Teaching:

Course: [CPS2232] Data Structures
Course Average Rating: 4.75
Comments:
  - [4.9] Best professor ever
  - [4.6] Challenging but fair

Course: [CPS1231] Java Programming
Course Average Rating: 4.65
Comments:
  - [4.5] Very clear explanation
  - [4.8] Excellent teaching style
```

#### 测试 4.2：查询不存在的教授

**输入：**
```
Please choose an option: 4
Enter professor name: Dr. Unknown
```

**预期输出：**
```
Professor named Dr. Unknown not found!
```

---

### 5. 显示教授排名 (Show professor ranking)

#### 测试 5.1：全局教授排名

**输入：**
```
Please choose an option: 5

Please choose ranking type:
1. Overall professor ranking
2. Professor ranking within a course
Please choose: 1

======== Overall Professor Ranking ========
Show top how many? (Enter 0 to show all): 0
```

**预期输出：**
```
1. Dr. Smith - Average: 4.70
2. Dr. Brown - Average: 4.55
3. Dr. Wang - Average: 4.20
4. Dr. Davis - Average: 4.05
5. Dr. Lee - Average: 4.00
6. Dr. Martinez - Average: 3.80
7. Dr. Johnson - Average: 3.75
```

#### 测试 5.2：全局教授排名（显示前3名）

**输入：**
```
Please choose an option: 5

Please choose ranking type:
1. Overall professor ranking
2. Professor ranking within a course
Please choose: 1

======== Overall Professor Ranking ========
Show top how many? (Enter 0 to show all): 3
```

**预期输出：**
```
1. Dr. Smith - Average: 4.70
2. Dr. Brown - Average: 4.55
3. Dr. Wang - Average: 4.20
```

#### 测试 5.3：课程内教授排名

**输入：**
```
Please choose an option: 5

Please choose ranking type:
1. Overall professor ranking
2. Professor ranking within a course
Please choose: 2
Enter course ID: CPS1231
```

**预期输出：**
```
======== Course [CPS1231] Java Programming Professor Ranking ========
1. Dr. Smith - Average: 4.65 (2 ratings)
2. Dr. Lee - Average: 4.20 (1 ratings)
3. Dr. Johnson - Average: 3.75 (2 ratings)
```

#### 测试 5.4：课程内教授排名（课程不存在）

**输入：**
```
Please choose an option: 5

Please choose ranking type:
1. Overall professor ranking
2. Professor ranking within a course
Please choose: 2
Enter course ID: INVALID
```

**预期输出：**
```
Course with ID INVALID not found!
```

---

### 6. 保存数据 (Save data)

#### 测试 6.1：保存数据到文件

**输入：**
```
Please choose an option: 6
```

**预期输出：**
```
Saving data to /Users/cxy/Desktop/finalProject/data.csv...
Data saved successfully to file: /Users/cxy/Desktop/finalProject/data.csv
```

---

### 7. 重新加载数据 (Reload data)

#### 测试 7.1：从文件重新加载数据

**输入：**
```
Please choose an option: 7
```

**预期输出：**
```
Loading data...
Rating added successfully!
Rating added successfully!
... (repeated for each rating)
Data loaded successfully from file: /Users/cxy/Desktop/finalProject/data.csv
```

---

### 0. 退出程序 (Exit and save)

#### 测试 0.1：正常退出

**输入：**
```
Please choose an option: 0
```

**预期输出：**
```
Saving data to /Users/cxy/Desktop/finalProject/data.csv...
Data saved successfully to file: /Users/cxy/Desktop/finalProject/data.csv
Thanks for using! Goodbye!
```

---

## 边界测试

### 输入非法菜单选项

**输入：**
```
Please choose an option: 99
```

**预期输出：**
```
Invalid option, please try again!
```

### 输入非数字菜单选项

**输入：**
```
Please choose an option: abc
```

**预期输出：**
```
Invalid option, please try again!
```

---

## 综合测试场景

### 场景：添加新评分并验证

1. 添加新评分
2. 按课程ID查询验证
3. 保存数据
4. 重新加载数据
5. 再次查询验证数据持久化

**步骤1 - 添加评分：**
```
Please choose an option: 1
Enter course ID: TEST001
Enter course name: Test Course
Enter professor name: Dr. Test
Enter rating (0-5): 5.0
Enter comment: Perfect score test
```
输出：`Rating added successfully!`

**步骤2 - 查询验证：**
```
Please choose an option: 2
Enter course ID: TEST001
```
输出：显示新添加的课程和评分信息

**步骤3 - 保存：**
```
Please choose an option: 6
```
输出：`Data saved successfully to file: ...`

**步骤4 - 重新加载：**
```
Please choose an option: 7
```
输出：加载成功信息

**步骤5 - 再次查询：**
```
Please choose an option: 2
Enter course ID: TEST001
```
输出：数据仍然存在，验证持久化成功

---

## 测试数据汇总

| 课程ID | 课程名称 | 教授 | 评分数 | 平均分 |
|--------|----------|------|--------|--------|
| CPS1231 | Java Programming | Dr. Smith | 2 | 4.65 |
| CPS1231 | Java Programming | Dr. Johnson | 2 | 3.75 |
| CPS1231 | Java Programming | Dr. Lee | 1 | 4.20 |
| CPS2232 | Data Structures | Dr. Smith | 2 | 4.75 |
| CPS2232 | Data Structures | Dr. Wang | 2 | 4.20 |
| MATH2413 | Calculus I | Dr. Lee | 2 | 3.90 |
| MATH2413 | Calculus I | Dr. Brown | 2 | 4.60 |
| ENG1301 | English Composition | Dr. Davis | 2 | 4.05 |
| PHYS2325 | University Physics | Dr. Brown | 2 | 4.50 |
| PHYS2325 | University Physics | Dr. Martinez | 2 | 3.80 |

---

*文档版本：1.0*
*最后更新：2025-12-04*

