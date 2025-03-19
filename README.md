# Jwt-starter 项目说明文档

## 项目简介

Jwt-starter 是一个基于 Spring Boot 框架的快速启动项目，用于实现 JWT (JSON Web Token) 认证机制。该项目提供了一系列的基础设施，包括用户注册、登录、获取用户列表以及注销功能。

## 功能模块

### 用户控制器 (`UserController`)

- **注册 (`/api/register`)**: 允许新用户注册。
- **登录 (`/api/login`)**: 验证用户登录并生成 JWT 令牌。
- **获取用户列表 (`/api/users`)**: 验证用户的 JWT 令牌并返回用户列表。
- **注销 (`/api/logout`)**: 使 JWT 令牌失效。

### 安全配置 (`SecurityConfig`)

- 配置 Spring Security 过滤器和规则，包括 JWT 令牌的验证。

### JWT 工具类 (`JwtUtil`)

- 生成和验证 JWT 令牌。
- 管理令牌黑名单。

### 用户服务 (`UserService`)

- 处理用户业务逻辑，如注册和登录验证。

### 异常处理 (`GlobalExceptionHandler`)

- 全局异常处理，特别是处理 JWT 相关的异常。

## 技术栈

- **Spring Boot**: 应用程序框架。
- **Spring Security**: 安全框架。
- **JWT**: 用于认证的令牌。
- **MySQL**: 数据库。
- **MyBatis-Plus**: ORM 框架。
- **Lombok**: 简化实体类代码。

## 如何使用

1. 克隆项目到本地。
2. 配置 `application.yml` 中的数据库连接信息。
3. 使用 Maven 构建项目。
4. 运行 `StarterApplication` 启动项目。

## API 端点

### POST `/api/register`

- **请求体**: `User` 对象，包含 `username` 和 `password`。
- **响应**: 注册成功的 `User` 对象。

### POST `/api/login`

- **请求体**: `User` 对象，包含 `username` 和 `password`。
- **响应**: 包含 JWT 令牌的 `Map` 对象。

### GET `/api/users`

- **请求头**: `Authorization`，格式为 `Bearer <token>`。
- **响应**: 用户列表。

### POST `/api/logout`

- **请求头**: `Authorization`，格式为 `Bearer <token>`。
- **响应**: 注销成功的消息。

## 数据库模型

### User

- `id`: 用户ID。
- `username`: 用户名。
- `password`: 密码。

## 项目结构

```
Jwt-starter
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── daisyPig
    │   │           ├── config
    │   │           ├── controller
    │   │           ├── entity
    │   │           ├── exception
    │   │           ├── filter
    │   │           ├── mapper
    │   │           ├── service
    │   │           ├── StarterApplication.java
    │   │           └── utils
    │   └── resources
    │       └── application.yml
    └── test
        └── java
            └── JwtExample.java
```

## 贡献指南

欢迎对项目进行贡献。请先讨论您想要更改的内容，然后提交拉取请求。

## 许可证

该项目采用 MIT 许可证。有关详细信息，请查看 LICENSE 文件。
