# 备考智能驾驶舱 (后端 - Spring Boot REST API)

本项目是“备考智能驾驶舱”的后端部分，一个使用 Spring Boot 构建的 RESTful API 服务，旨在为前端 Vue 应用提供业务逻辑处理和数据持久化支持。

## 项目目的

为前端应用提供稳定、安全的接口，用于管理备考过程中的各项数据，包括任务、笔记、课程进度、学习日志、错题、知识库等，并将这些数据持久化存储在数据库中。

## 技术栈

*   **框架:** Spring Boot 3.x
*   **语言:** Java 17+
*   **Web:** Spring Web (Spring MVC for REST)
*   **数据持久化:** Spring Data JPA, Hibernate (JPA Provider)
*   **数据库:** MySQL (或 PostgreSQL，根据配置)
*   **构建工具:** Maven (或 Gradle)
*   **辅助库:** Lombok (简化代码)
*   **API 风格:** RESTful API (使用 JSON 进行数据交换)

## 项目结构 (典型分层)

*   `src/main/java/com/example/gwy_backend/`: Java 源代码根目录。
    *   `GwyBackendApplication.java`: Spring Boot 启动类。
    *   `config/`: 配置类 (如 `WebConfig.java` 用于 CORS)。
    *   `controller/`: Controller 层，处理 HTTP 请求，定义 API 端点。
    *   `service/`: Service 层接口。
    *   `service/impl/`: Service 层实现类，包含业务逻辑。
    *   `repository/`: Repository 层接口，继承 JpaRepository，定义数据访问方法。
    *   `entity/`: Entity 层，定义数据模型，使用 JPA 注解映射数据库表。
*   `src/main/resources/`: 资源文件目录。
    *   `application.properties`: 应用配置文件（数据库连接、服务器端口、JPA 设置等）。
    *   `static/`, `templates/`: (本项目中未使用，因为是纯 API 服务)。
*   `pom.xml`: Maven 项目配置文件 (依赖管理、插件配置)。

## 核心功能与实现

后端采用分层架构，通过 RESTful API 对外提供服务：

*   **Controller 层:**
    *   使用 `@RestController` 和 `@RequestMapping` 定义 API 基础路径。
    *   使用 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping` 映射具体的 HTTP 方法和路径到处理方法。
    *   使用 `@PathVariable`, `@RequestParam`, `@RequestBody` 接收来自前端的参数和数据。
    *   注入 `Service` 接口，调用 Service 方法处理业务。
    *   使用 `ResponseEntity` 封装 HTTP 响应，返回 JSON 数据和状态码。
    *   配置了全局 CORS (`WebConfig.java`) 以允许指定来源的前端应用访问。
*   **Service 层:**
    *   使用 `@Service` 标记实现类。
    *   注入 `Repository` 接口。
    *   实现核心业务逻辑，例如数据验证、组合数据、事务管理 (`@Transactional`)。
    *   调用 Repository 方法进行数据库操作。
*   **Repository 层:**
    *   使用 `@Repository` 标记接口。
    *   继承 `JpaRepository` 以获得基本的 CRUD 功能。
    *   通过**方法命名约定**或使用 `@Query` 注解定义特定的数据库查询。
*   **Entity 层:**
    *   使用 `@Entity` 标记类为数据库实体。
    *   使用 `@Id`, `@GeneratedValue`, `@Column`, `@Lob`, `@ElementCollection`, `@Index`, `@Table` 等 JPA 注解来映射 Java 字段到数据库表的列和约束。
    *   使用 `@PrePersist`, `@PreUpdate` 等生命周期回调自动设置时间戳等字段。
    *   使用 Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) 简化样板代码。
*   **数据持久化:**
    *   通过 Spring Data JPA 和 Hibernate 将 Entity 对象的操作转换为 SQL 语句，与配置的 MySQL (或 PostgreSQL) 数据库进行交互。
    *   通过 `application.properties` 配置数据库连接信息和 JPA/Hibernate 行为（如 `ddl-auto`, `show-sql`）。

## API 端点概览 (示例)

*   `GET /api/timeline/tasks/grouped`: 获取按阶段分组的时间轴任务。
*   `PATCH /api/timeline/tasks/{taskId}`: 更新任务完成状态。
*   `GET /api/course-tracker`: 获取课程追踪信息。
*   `PATCH /api/course-tracker`: 更新课程追踪信息。
*   `GET /api/pomodoro/settings`: 获取番茄钟设置。
*   `PATCH /api/pomodoro/settings`: 更新番茄钟设置。
*   `POST /api/pomodoro/log`: 添加学习日志。
*   `GET /api/pomodoro/log/recent`: 获取最近的学习日志。
*   `DELETE /api/pomodoro/log/all`: 清空所有学习日志。
*   `GET /api/errors`: 获取错题记录 (支持 `?subject=` 筛选)。
*   `POST /api/errors`: 添加错题记录。
*   `PATCH /api/errors/{id}/review`: 标记错题为已复习。
*   `DELETE /api/errors/{id}`: 删除错题记录。
*   `GET /api/knowledge`: 获取知识库条目 (支持 `?category=` 和 `?search=` 筛选)。
*   `POST /api/knowledge`: 添加知识库条目。
*   `DELETE /api/knowledge/{id}`: 删除知识库条目。
*   `GET /api/notes`: 获取所有笔记记录 (按时间排序)。
*   `POST /api/notes`: 创建新的笔记记录。
*   `GET /api/goals`: 获取学习目标。
*   `POST /api/goals`: 添加学习目标。
*   `PATCH /api/goals/{id}/toggle`: 切换学习目标完成状态。
*   `DELETE /api/goals/{id}`: 删除学习目标。

## 运行方式

1.  确保已安装兼容的 JDK (如 17+) 和 Maven。
2.  确保 MySQL (或配置的其他数据库) 服务正在运行，并且数据库、用户、权限已按 `application.properties` 配置好。
3.  在项目根目录下执行 Maven 命令运行：
    ```bash
    mvn spring-boot:run
    ```
    或者先打包再运行：
    ```bash
    mvn clean package -DskipTests
    java -jar target/gwy-backend-0.0.1-SNAPSHOT.jar
    ```
4.  应用默认启动在 `http://localhost:8080`。