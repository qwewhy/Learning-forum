# SpringBoot Learning Forum Project Documentation - Assignment 3

<div align="center">
  <img src="https://www.chemviz3d.com/chemistry-3d-logo.svg" alt="Learning Forum Logo" width="200"/>
  <h3>A Professional Community for Exchanging Learning Resources</h3>
  <p>A full-featured forum system based on Java SpringBoot, integrating mainstream technology stacks and enterprise-level performance optimization.</p>
</div>

## 📚 Project Overview
**Project Name**: SpringBoot LearningForum — Deployed on Tencent Cloud Server
**Note**: Due to demonstration requirements in the assignment, server API documentation and other information had to be placed in this file in plain text. Therefore, it is not guaranteed that the server will still be running when you grade it, as the server may be attacked and become unusable.
**Author**: Hongyuan Wang

**Project Demo**: [API Documentation](http://43.165.196.25/api/doc.html#/home) API Documentation Account: root Password: Why20020721

## 🚀 Core Highlights

- **High-Performance Architecture**: Multi-level caching design, hot data detection, rate limiting, and circuit breaking protection.
- **Enterprise-Grade Security**: RBAC permission system, anti-crawler strategies, dynamic blacklist.
- **Full-Text Search**: Elasticsearch-based tokenized search, supporting complex query conditions.
- **Efficient Development**: Custom code generator, adhering to Alibaba Java Coding Guidelines.
- **Comprehensive Testing**: JUnit5 unit tests.
- **Cloud-Native Support**: Supports containerized deployment and multi-environment configuration.

## 🔨 Technology Stack and Features

### Mainstream Frameworks & Features
- **Spring Boot 2.7.x**: Core framework, providing auto-configuration and rapid development capabilities.
- **Spring MVC**: Web development framework for handling HTTP requests.
- **MyBatis + MyBatis Plus**: ORM framework, simplifying data access.
  - Integrated MyBatisX plugin, supporting one-click CRUD code generation.
  - Enabled pagination feature for efficient data pagination queries.
- **Spring AOP**: Aspect-Oriented Programming, used for logging, permission checks, and other cross-cutting concerns.
- **Spring Scheduler**: Scheduled task framework, used for data synchronization, cache updates, etc.
- **Spring Transaction Management**: Declarative transactions via annotations, ensuring data consistency.

### Data Storage Solutions
- **MySQL**: Relational database for storing core business data.
  - Custom index optimization to improve query performance.
  - Druid connection pool monitoring for real-time detection of slow SQL.
- **Redis**: In-memory database.
  - Distributed Session storage, supporting cluster deployment.
  - Hot data caching to reduce database pressure.
  - BitMap for efficient data statistics.
  - Lua scripts to ensure atomic operations.
- **Elasticsearch**: Search engine.
  - Custom tokenizer to improve search accuracy.
  - Static/dynamic data separation strategy to reduce data synchronization costs.
  - Scheduled incremental synchronization mechanism.
- **Tencent Cloud COS**: Object storage for file uploads and CDN acceleration.

### Tools and Libraries
- **Easy Excel**: Efficient Excel processing library, supporting large file import/export.
- **Hutool**: Java utility collection, providing a wealth of utility methods.
- **Apache Commons Lang3**: Java basic utility library.
- **Lombok**: Simplifies JavaBean development through annotations.
- **Caffeine**: Local cache library, providing JVM-level high-speed caching.
- **HotKey**: Hot data detection, automatically caching frequently accessed content.
- **Redisson**: Distributed locks and enriched Redis client.

### Security and Performance Optimization
- **Sa-Token**: Lightweight permission authentication framework.
  - Implements mutually exclusive login on the same device to prevent account sharing.
  - RBAC-based permission control.
- **Sentinel**: Flow control and circuit breaking.
  - API rate limiting protection.
  - Hot parameter rate limiting.
  - Service circuit breaking with automatic fallback.
- **Custom Anti-Crawler Strategy**:
  - Redis-based access frequency statistics.
  - Lua scripts for atomic counting.
  - Dynamic blacklist mechanism.
- **Nacos**: Service configuration center.
  - Dynamic updates of system configurations.
  - Real-time adjustment of whitelists and blacklists.

### Development and Debugging Tools
- **Swagger + Knife4j**: API documentation generation and testing.
- **Custom Code Generator**: One-click generation of Service, Controller, and entity class code.
- **Spring Boot Devtools**: Hot deployment, improving development efficiency.
- **Logback**: Logging framework, supporting multi-environment log configuration.

## GIT Records and API Documentation

![git1](public/git1.png)
![git2](public/git2.png)
![API-Doc](public/API-Doc.png)

## 🌟 Business Functions

### User System
- Login, registration, logout, personal information update.
- Sa-Token based permission management.
- WeChat Open Platform login integration.
- Mutually exclusive login mechanism on the same device.

### Core Forum Functions
- **Post Management**:
  - Create, delete, edit, update posts.
  - Dual search mechanism (database and ES).
  - Tagging system, supporting multi-dimensional categorization.
- **Interaction System**:
  - Like/unlike functionality.
  - Favorite/unfavorite functionality.
  - User comment system.
  - Post view statistics.
- **Search System**:
  - Elasticsearch-based full-text search.
  - Supports multi-criteria search (title, content, tags).
  - Search result highlighting.
  - Search recommendation functionality.

### Content Operations
- Post data synchronization mechanism:
  - Full ES synchronization task.
  - Incremental ES synchronization scheduled task.
- File upload system:
  - Supports file uploads categorized by business.
  - Supports multiple file types (images, documents, etc.).
  - Automatic thumbnail generation.

### Environment Requirements
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- Elasticsearch 7.x (Optional)
- Tencent Cloud Account (Optional, for object storage)

### Local Development Environment Setup

#### 1. Clone Project
```bash
git clone https://github.com/HongyuanWang/SpringBoot-LearningForum.git
cd SpringBoot-LearningForum
```

#### 2. MySQL Database Configuration
1. Modify the database configuration in `application.yml`:
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/learning_forum
    username: root
    password: 123456
```

2. Execute the database statements in `sql/create_table.sql` to automatically create the database and tables.

3. Start the project and visit http://localhost:8101/api/doc.html to open the API documentation. You can debug APIs online without writing frontend code.

#### 3. Redis Distributed Login Configuration
1. Modify the Redis configuration in `application.yml`:
```yaml
spring:
  redis:
    database: 1
    host: localhost
    port: 6379
    timeout: 5000
    password: 123456
```

2. Modify the session storage type in `application.yml`:
```yaml
spring:
  session:
    store-type: redis
```

#### 4. Elasticsearch Search Engine Configuration
1. Modify the Elasticsearch configuration in `application.yml`:
```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    username: root
    password: 123456
```

2. Copy the content of the `sql/post_es_mapping.json` file and use Elasticsearch API or Kibana Dev Tools to create an index (equivalent to creating a table in a database):
```
PUT post_v1
{
  Parameters are in the sql/post_es_mapping.json file
}
```

3. Enable synchronization tasks to synchronize posts from the database to Elasticsearch: Find the `FullSyncPostToEs` and `IncSyncPostToEs` files in the `job` directory, uncomment the `@Component` annotation, and run the program again to trigger synchronization:
```java
// todo Uncomment to enable the task
//@Component
```

#### 5. Tencent Cloud COS Configuration (Optional)
1. Configure Tencent Cloud COS parameters in `application.yml`:
```yaml
cos:
  client:
    accessKey: accessKey
    secretKey: secretKey
    region: ap-guangzhou
    bucket: your-bucket-name
```

### Business Code Generator Usage
Supports automatic generation of Service, Controller, and data model code. Combined with the MyBatisX plugin, it can quickly develop basic CRUD functionalities.

Find the `generate.CodeGenerator` class, modify the generation parameters and path, and you can comment out unnecessary generation logic. Then run it:
```java
// Specify generation parameters
String packageName = "com.HongyuanWang.learningforum";
String dataName = "User Comment"; // e.g., "用户评论" (User Comment)
String dataKey = "userComment";
String upperDataKey = "UserComment";
```

After generating the code, you can move it to the actual project and modify it according to the `// todo` comments based on your business needs.

## 🏗️ Project Architecture

### System Architecture Diagram
![System Architecture Diagram](public/structure.png)

### Code Structure
```
F:\GITCODEHERE\LEARNING-FORUM\SRC
|   .DS_Store
|
+---main
|   +---java
|   |   +---com
|   |   |   \---HongyuanWang
|   |   |       \---learningforum
|   |   |           |   MainApplication.java
|   |   |           |
|   |   |           +---annotation
|   |   |           |       AuthCheck.java
|   |   |           |
|   |   |           +---aop
|   |   |           |       AuthInterceptor.java
|   |   |           |       LogInterceptor.java
|   |   |           |
|   |   |           +---blackfilter
|   |   |           |       BlackIpFilter.java
|   |   |           |       BlackIpUtils.java
|   |   |           |       NacosListener.java
|   |   |           |
|   |   |           +---common
|   |   |           |       BaseResponse.java
|   |   |           |       DeleteRequest.java
|   |   |           |       ErrorCode.java
|   |   |           |       PageRequest.java
|   |   |           |       ResultUtils.java
|   |   |           |
|   |   |           +---config
|   |   |           |       CorsConfig.java
|   |   |           |       CosClientConfig.java
|   |   |           |       HotKeyConfig.java
|   |   |           |       JsonConfig.java
|   |   |           |       MyBatisPlusConfig.java
|   |   |           |       RedissonConfig.java
|   |   |           |       WxOpenConfig.java
|   |   |           |
|   |   |           +---constant
|   |   |           |       CommonConstant.java
|   |   |           |       FileConstant.java
|   |   |           |       RedisConstant.java
|   |   |           |       UserConstant.java
|   |   |           |
|   |   |           +---controller
|   |   |           |       FileController.java
|   |   |           |       PostController.java
|   |   |           |       PostFavourController.java
|   |   |           |       PostThumbController.java
|   |   |           |       QuestionBankController.java
|   |   |           |       QuestionBankQuestionController.java
|   |   |           |       QuestionController.java
|   |   |           |       UserController.java
|   |   |           |       WxMpController.java
|   |   |           |
|   |   |           +---esdao
|   |   |           |       PostEsDao.java
|   |   |           |       QuestionEsDao.java
|   |   |           |
|   |   |           +---exception
|   |   |           |       BusinessException.java
|   |   |           |       GlobalExceptionHandler.java
|   |   |           |       ThrowUtils.java
|   |   |           |
|   |   |           +---generate
|   |   |           |       CodeGenerator.java
|   |   |           |
|   |   |           +---job
|   |   |           |   +---cycle
|   |   |           |   |       IncSyncPostToEs.java
|   |   |           |   |       IncSyncQuestionToEs.java
|   |   |           |   |
|   |   |           |   \---once
|   |   |           |           FullSyncPostToEs.java
|   |   |           |           FullSyncQuestionToEs.java
|   |   |           |
|   |   |           +---manager
|   |   |           |       CosManager.java
|   |   |           |       CounterManager.java
|   |   |           |
|   |   |           +---mapper
|   |   |           |       PostFavourMapper.java
|   |   |           |       PostMapper.java
|   |   |           |       PostThumbMapper.java
|   |   |           |       QuestionBankMapper.java
|   |   |           |       QuestionBankQuestionMapper.java
|   |   |           |       QuestionMapper.java
|   |   |           |       UserMapper.java
|   |   |           |
|   |   |           +---model
|   |   |           |   +---dto
|   |   |           |   |   +---file
|   |   |           |   |   |       UploadFileRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---post
|   |   |           |   |   |       PostAddRequest.java
|   |   |           |   |   |       PostEditRequest.java
|   |   |           |   |   |       PostEsDTO.java
|   |   |           |   |   |       PostQueryRequest.java
|   |   |           |   |   |       PostUpdateRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---postfavour
|   |   |           |   |   |       PostFavourAddRequest.java
|   |   |           |   |   |       PostFavourQueryRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---postthumb
|   |   |           |   |   |       PostThumbAddRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---question
|   |   |           |   |   |       QuestionAddRequest.java
|   |   |           |   |   |       QuestionBatchDeleteRequest.java
|   |   |           |   |   |       QuestionEditRequest.java
|   |   |           |   |   |       QuestionEsDTO.java
|   |   |           |   |   |       QuestionQueryRequest.java
|   |   |           |   |   |       QuestionUpdateRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---questionBank
|   |   |           |   |   |       QuestionBankAddRequest.java
|   |   |           |   |   |       QuestionBankEditRequest.java
|   |   |           |   |   |       QuestionBankQueryRequest.java
|   |   |           |   |   |       QuestionBankUpdateRequest.java
|   |   |           |   |   |
|   |   |           |   |   +---questionBankQuestion
|   |   |           |   |   |       QuestionBankQuestionAddRequest.java
|   |   |           |   |   |       QuestionBankQuestionBatchAddRequest.java
|   |   |           |   |   |       QuestionBankQuestionBatchRemoveRequest.java
|   |   |           |   |   |       QuestionBankQuestionQueryRequest.java
|   |   |           |   |   |       QuestionBankQuestionRemoveRequest.java
|   |   |           |   |   |       QuestionBankQuestionUpdateRequest.java
|   |   |           |   |   |
|   |   |           |   |   \---user
|   |   |           |   |           UserAddRequest.java
|   |   |           |   |           UserLoginRequest.java
|   |   |           |   |           UserQueryRequest.java
|   |   |           |   |           UserRegisterRequest.java
|   |   |           |   |           UserUpdateMyRequest.java
|   |   |           |   |           UserUpdateRequest.java
|   |   |           |   |
|   |   |           |   +---entity
|   |   |           |   |       Post.java
|   |   |           |   |       PostFavour.java
|   |   |           |   |       PostThumb.java
|   |   |           |   |       Question.java
|   |   |           |   |       QuestionBank.java
|   |   |           |   |       QuestionBankFavour.java
|   |   |           |   |       QuestionBankQuestion.java
|   |   |           |   |       QuestionBankThumb.java
|   |   |           |   |       User.java
|   |   |           |   |
|   |   |           |   +---enums
|   |   |           |   |       FileUploadBizEnum.java
|   |   |           |   |       UserRoleEnum.java
|   |   |           |   |
|   |   |           |   \---vo
|   |   |           |           LoginUserVO.java
|   |   |           |           PostVO.java
|   |   |           |           QuestionBankQuestionVO.java
|   |   |           |           QuestionBankVO.java
|   |   |           |           QuestionVO.java
|   |   |           |           UserVO.java
|   |   |           |
|   |   |           +---satoken
|   |   |           |       DeviceUtils.java
|   |   |           |       SaTokenConfigure.java
|   |   |           |       StpInterfaceImpl.java
|   |   |           |
|   |   |           +---sentinel
|   |   |           |       SentinelTest.java
|   |   |           |
|   |   |           +---service
|   |   |           |   |   PostFavourService.java
|   |   |           |   |   PostService.java
|   |   |           |   |   PostThumbService.java
|   |   |           |   |   QuestionBankQuestionService.java
|   |   |           |   |   QuestionBankService.java
|   |   |           |   |   QuestionService.java
|   |   |           |   |   UserService.java
|   |   |           |   |
|   |   |           |   \---impl
|   |   |           |           PostFavourServiceImpl.java
|   |   |           |           PostServiceImpl.java
|   |   |           |           PostThumbServiceImpl.java
|   |   |           |           QuestionBankQuestionServiceImpl.java
|   |   |           |           QuestionBankServiceImpl.java
|   |   |           |           QuestionServiceImpl.java
|   |   |           |           UserServiceImpl.java
|   |   |           |
|   |   |           +---utils
|   |   |           |       NetUtils.java
|   |   |           |       SpringContextUtils.java
|   |   |           |       SqlUtils.java
|   |   |           |
|   |   |           \---wxmp
|   |   |               |   WxMpConstant.java
|   |   |               |   WxMpMsgRouter.java
|   |   |               |
|   |   |               \---handler
|   |   |                       EventHandler.java
|   |   |                       MessageHandler.java
|   |   |                       SubscribeHandler.java
|   |   |
|   |   \---generator
|   |       \---service
|   |           |   PostFavourService.java
|   |           |   PostService.java
|   |           |   PostThumbService.java
|   |           |   QuestionBankQuestionService.java
|   |           |   QuestionBankService.java
|   |           |   QuestionService.java
|   |           |   UserService.java
|   |           |
|   |           \---impl
|   |                   PostFavourServiceImpl.java
|   |                   PostServiceImpl.java
|   |                   PostThumbServiceImpl.java
|   |                   QuestionBankQuestionServiceImpl.java
|   |                   QuestionBankServiceImpl.java
|   |                   QuestionServiceImpl.java
|   |                   UserServiceImpl.java
|   |
|   \---resources
|       |   application-prod.yml
|       |   application-test.yml
|       |   application.yml
|       |   banner.txt
|       |   test_excel.xlsx
|       |
|       +---mapper
|       |       PostFavourMapper.xml
|       |       PostMapper.xml
|       |       PostThumbMapper.xml
|       |       UserMapper.xml
|       |
|       +---META-INF
|       |       additional-spring-configuration-metadata.json
|       |
|       \---templates
|           |   TemplateController.java.ftl
|           |   TemplateService.java.ftl
|           |   TemplateServiceImpl.java.ftl
|           |
|           \---model
|                   TemplateAddRequest.java.ftl
|                   TemplateEditRequest.java.ftl
|                   TemplateQueryRequest.java.ftl
|                   TemplateUpdateRequest.java.ftl
|                   TemplateVO.java.ftl
|
\---test
    |   .DS_Store
    |
    \---java
        \---com
            \---HongyuanWang
                \---learningforum
                    |   MainApplicationTests.java
                    |
                    +---manager
                    |       CosManagerTest.java
                    |
                    +---mapper
                    |       PostFavourMapperTest.java
                    |       PostMapperTest.java
                    |
                    +---service
                    |       PostFavourServiceTest.java
                    |       PostThumbServiceTest.java
                    |       UserServiceTest.java
                    |
                    \---utils
                            EasyExcelTest.java
```

### Layered Design
- **Controller Layer**: Responsible for receiving requests, parameter validation, and returning results.
- **Service Layer**: Implements business logic.
- **DAO Layer**: Data Access Layer, interacts with the database.
- **Model Layer**: Data models, including entities, DTOs, VOs, etc.
- **Utils**: Utility classes, providing common functionalities.
- **Config**: Configuration classes, managing system configurations.
- **Job**: Scheduled tasks, implementing data synchronization, etc.
- **AOP**: Aspect-Oriented Programming, implementing logging, permissions, and other cross-cutting concerns.

## 📈 Performance Optimization Practices

### Database Optimization
- Improve query performance through custom index optimization.
- Use Druid connection pool to monitor SQL execution.
- Enhance performance with MyBatis Plus batch operations.
- Avoid large transactions, ensuring reasonable transaction granularity.
- Reduce database load with pagination queries.

### Cache Optimization
- Adopt a multi-level cache architecture:
  - Caffeine local cache → Redis distributed cache → Database
- Use HotKey to automatically detect and cache hot data.
- Store user operation records with Redis BitMap, saving 90%+ storage space.
- Set reasonable cache expiration times to prevent cache avalanches.

### Concurrency Handling
- Use CompletableFuture for parallel batch processing.
- Solve concurrency safety issues with Redisson distributed locks.
- Ensure counter atomicity with Redis Lua scripts.
- Use thread pools rationally to avoid resource waste.

### System Protection
- Use Sentinel for API rate limiting and service circuit breaking.
- Configure hot parameter rate limiting for frequently accessed APIs.
- Implement service degradation with FallbackHandler to ensure system availability.
- Persist rate limiting rules locally using a pull model.

## 🔐 Security Features

### User Authentication and Authorization
- User authentication based on Sa-Token.
- RBAC permission model for flexible permission control.
- Custom permission annotations for globally unified validation.
- Mutually exclusive login on the same device to prevent account sharing.

### Data Security
- Encrypted storage of sensitive information.
- XSS defense and SQL injection protection.
- Solution for long integer precision loss.
- Global exception handling to prevent sensitive information leakage.

### Anti-Crawler Strategies
- Redis-based user access frequency statistics.
- Multi-level protection mechanism: Warning → Human verification → Account ban.
- Lua scripts for efficient counting and atomicity checks.
- Dynamic IP blacklist updates via Nacos.

## 🌐 Deployment and Operations

### Local Development Environment
- Supports hot deployment, improving development efficiency.
- Multi-environment configuration to adapt to different development stages.
- Detailed logging configuration for easy debugging.

### Test Environment
- JUnit5 unit tests covering core business logic.
- Automatic generation of mock test data.
- Test report generation and analysis.

### Production Environment Deployment
- Docker-based containerized deployment.
- Nginx reverse proxy to resolve cross-origin issues.
- Simplified operations using Baota Linux Panel.
- Supports CI/CD automated deployment.

## 🔄 Future Plans

- [ ] Integrate Spring Cloud microservices architecture.
- [ ] Add real-time message push functionality.
- [ ] Introduce AI intelligent recommendation system.
- [ ] Add user behavior analysis module.
- [ ] Optimize mobile-end adaptation.
- [ ] Improve internationalization support.

## 🤝 Contribution Guidelines

Issues and Pull Requests are welcome!

1. Fork this repository.
2. Create your feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m 'Add some amazing feature'`).
4. Push your changes to the branch (`git push origin feature/amazing-feature`).
5. Submit a Pull Request.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 About the Author

Hongyuan Wang - [Personal Homepage](https://github.com/HongyuanWang)

## Acknowledgements

Thanks to all developers who contributed to this project and the open-source communities that provided technical support.

---

<div align="center">
  <p>If this project is helpful to you, please give it a ⭐️!</p>
  <p>The project is continuously being updated, please stay tuned...</p>
</div>