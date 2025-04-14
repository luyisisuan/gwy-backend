# 备考智能驾驶舱 (后端 - Spring Boot REST API)

本项目是"备考智能驾驶舱"的后端部分，一个使用 Spring Boot 构建的 RESTful API 服务，旨在为前端 Vue 应用提供业务逻辑处理和数据持久化支持。

## 项目目的

为前端应用提供稳定、安全的接口，用于管理备考过程中的各项数据，包括任务、笔记、课程进度、学习日志、错题、知识库等，并将这些数据持久化存储在数据库中。

## 技术栈

* **框架:** Spring Boot 3.x
* **语言:** Java 17+
* **Web:** Spring Web (Spring MVC for REST)
* **数据持久化:** Spring Data JPA, Hibernate (JPA Provider)
* **数据库:** MySQL (或 PostgreSQL，根据配置)
* **构建工具:** Maven (或 Gradle)
* **辅助库:** Lombok (简化代码)
* **API 风格:** RESTful API (使用 JSON 进行数据交换)

## 项目结构

* `src/main/java/com/example/gwybackend/`: 主要源代码目录
  * `controller/`: REST API 控制器，处理 HTTP 请求
    * `TimelineController.java`: 时间轴相关接口
    * `CourseTrackerController.java`: 课程追踪相关接口
    * `PomodoroController.java`: 番茄钟和学习日志相关接口
    * `ErrorLogController.java`: 错题记录相关接口
    * `KnowledgeController.java`: 知识库相关接口
    * `NoteController.java`: 笔记相关接口
    * `GoalController.java`: 学习目标相关接口
    * 其他控制器
  * `service/`: 业务逻辑层，实现核心功能
    * 对应每个控制器的服务类
  * `repository/`: 数据访问层，与数据库交互
    * 对应每个实体的 JPA 仓库接口
  * `model/`: 数据模型/实体类
    * `TimelineTask.java`: 时间轴任务实体
    * `CourseTracker.java`: 课程追踪实体
    * `PomodoroSettings.java`: 番茄钟设置实体
    * `StudyLog.java`: 学习日志实体
    * `ErrorLog.java`: 错题记录实体
    * `KnowledgeItem.java`: 知识库条目实体
    * `Note.java`: 笔记实体
    * `Goal.java`: 学习目标实体
    * 其他实体类
  * `dto/`: 数据传输对象，用于 API 请求和响应
  * `exception/`: 自定义异常类
  * `config/`: 配置类
  * `util/`: 工具类
  * `GwyBackendApplication.java`: 应用程序入口类
* `src/main/resources/`: 资源文件目录
  * `application.properties`: 应用配置文件
  * `application-dev.properties`: 开发环境配置
  * `application-prod.properties`: 生产环境配置
* `src/test/`: 测试代码目录
* `pom.xml`: Maven 项目配置文件

## 核心功能与实现

### 数据模型设计

* **实体类设计:**
  * 使用 `@Entity`, `@Table` 等 JPA 注解定义实体类与数据库表的映射
  * 使用 `@Id`, `@GeneratedValue`, `@Column`, `@Lob`, `@ElementCollection`, `@Index`, `@Table` 等 JPA 注解来映射 Java 字段到数据库表的列和约束
  * 使用 `@PrePersist`, `@PreUpdate` 等生命周期回调自动设置时间戳等字段
  * 使用 Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) 简化样板代码

### 数据持久化

* 通过 Spring Data JPA 和 Hibernate 将 Entity 对象的操作转换为 SQL 语句，与配置的 MySQL (或 PostgreSQL) 数据库进行交互
* 通过 `application.properties` 配置数据库连接信息和 JPA/Hibernate 行为（如 `ddl-auto`, `show-sql`）
* 使用 JPA Repository 接口简化数据访问操作，支持基本的 CRUD 和自定义查询

### REST API 实现

* 使用 Spring MVC 的 `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` 等注解定义 RESTful API 端点
* 使用 `@RequestBody`, `@PathVariable`, `@RequestParam` 等注解处理请求参数
* 返回标准的 HTTP 状态码和 JSON 格式的响应数据
* 实现基于资源的 API 设计风格

### 错误处理

* 使用 `@ControllerAdvice` 和 `@ExceptionHandler` 实现全局异常处理
* 返回统一格式的错误响应，包含错误代码、消息和详情
* 针对不同类型的异常返回相应的 HTTP 状态码

### 业务逻辑

* 在 Service 层实现核心业务逻辑，如数据验证、处理和转换
* 使用事务管理确保数据一致性
* 实现数据过滤、排序和分页功能

## API 端点概览

* `GET /api/timeline/tasks/grouped`: 获取按阶段分组的时间轴任务
* `PATCH /api/timeline/tasks/{taskId}`: 更新任务完成状态
* `GET /api/course-tracker`: 获取课程追踪信息
* `PATCH /api/course-tracker`: 更新课程追踪信息
* `GET /api/pomodoro/settings`: 获取番茄钟设置
* `PATCH /api/pomodoro/settings`: 更新番茄钟设置
* `POST /api/pomodoro/log`: 添加学习日志
* `GET /api/pomodoro/log/recent`: 获取最近的学习日志
* `DELETE /api/pomodoro/log/all`: 清空所有学习日志
* `GET /api/errors`: 获取错题记录 (支持 `?subject=` 筛选)
* `POST /api/errors`: 添加错题记录
* `PATCH /api/errors/{id}/review`: 标记错题为已复习
* `DELETE /api/errors/{id}`: 删除错题记录
* `GET /api/knowledge`: 获取知识库条目 (支持 `?category=` 和 `?search=` 筛选)
* `POST /api/knowledge`: 添加知识库条目
* `DELETE /api/knowledge/{id}`: 删除知识库条目
* `GET /api/notes`: 获取所有笔记记录 (按时间排序)
* `POST /api/notes`: 创建新的笔记记录
* `GET /api/goals`: 获取学习目标
* `POST /api/goals`: 添加学习目标
* `PATCH /api/goals/{id}/toggle`: 切换学习目标完成状态
* `DELETE /api/goals/{id}`: 删除学习目标

## 安全性考虑

* 使用 HTTPS 保护数据传输
* 实现输入验证，防止恶意数据
* 使用参数化查询防止 SQL 注入
* 考虑添加认证和授权机制

## 运行方式

1. 确保已安装兼容的 JDK (如 17+) 和 Maven
2. 确保 MySQL (或配置的其他数据库) 服务正在运行，并且数据库、用户、权限已按 `application.properties` 配置好
3. 在项目根目录下执行 Maven 命令运行：
   ```bash
   mvn spring-boot:run
    ```
    或者先打包再运行：
    ```bash
    mvn clean package -DskipTests
    java -jar target/gwy-backend-0.0.1-SNAPSHOT.jar
    ```
4.  应用默认启动在 `http://localhost:8080`。