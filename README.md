# 🧙‍♂️ PostWizardREST

A high-performance, secure RESTful API for WordPress content management, built with modern Java Enterprise architectural patterns. 

PostWizardREST provides a robust complement to the default WordPress REST API, offering performance improvements in specific automation-heavy and enterprise deployment scenarios, enhanced security features, and advanced content management capabilities.

## ⚠️ Project Status

**Archived / Discontinued**

PostWizardREST is **no longer actively maintained**, and the associated service is **no longer available**.

This repository is preserved **for reference and educational purposes only**.  
No support, updates, security patches, or guarantees are provided.

---

> **Historical note**
> 
>  All documentation below reflects the system’s design, features, and API
> as they existed during active development. It is preserved for architectural
> reference and educational purposes.

---

## Why was PostWizardREST created?

PostWizardREST was designed as an **integration hub and server-side extension** for projects such as [PostWizardX3](https://github.com/Urbine/PostWizardX3), handling taxonomy creation, post management, and media-related workflows outside of WordPress’ runtime.

While the WordPress REST API is suitable for many use cases, it is intentionally constrained by WordPress’ execution model, plugin ecosystem, and PHP-based request lifecycle. PostWizardREST explored an alternative approach: **offloading critical content management workflows to a dedicated Java-based service**, while remaining fully compatible with the WordPress database and ecosystem.

This design was motivated by the need for greater control over execution, security boundaries, and data modeling, particularly in high-volume or automation-heavy scenarios.

By decoupling these responsibilities from WordPress, the project aimed to leverage established Enterprise Java capabilities, resulting in:

* **Direct database access**, avoiding repeated WordPress bootstrap and plugin execution overhead
* **Scheduled and batch jobs** with proper locking, retries, and concurrency control
* **Application-server–managed threading and resource pools**, instead of request-bound execution
* **A REST API decoupled from WordPress’ PHP execution model**, enabling reliable integration with external automation systems
* **Documentation-driven API design** with explicit contracts
* **Structured metadata handling** with cascading and relational consistency
* **Type-safe APIs** to reduce common runtime and data-shape errors
* **Explicit control over database connections, transactions, and isolation levels**
* **Extensibility by default**, without reliance on WordPress hooks or filters
* **Reduced attack surface**, by minimizing public exposure of WordPress endpoints commonly targeted by automated attacks against WordPress installations
* **Observability and audit logging**, enabling detection of abnormal behavior and performance bottlenecks
* **Developer-friendly modeling of taxonomies and custom fields** for Java-based teams
* **A WordPress domain abstraction**, designed with future integration into Jakarta EE or Spring ecosystems in mind, including message-driven and batch-oriented enterprise workflows
* **Compatibility with existing WordPress plugins**, including [Yoast SEO](https://yoast.com/) and [CompressX](https://compressx.io/)
* **JWT-based authentication**, role-based access control, and request validation
* **Consistent response formats and structured error handling**
* **Integration testing, CI/CD readiness, and container-friendly deployment**

In short, PostWizardREST was **not intended to replace WordPress**, but to **isolate automation-heavy, security-sensitive, or high-throughput operations** into a system better suited for those concerns, while still using WordPress as the content source of truth.

## ✨ Features

### Core Features
- Full CRUD operations for WordPress posts
- Advanced taxonomy management (categories, tags, custom taxonomies)
- Extensible metadata system
- Batch processing using Jakarta Concurrency
- Scheduled and automated content tasks

### Security Features
- JWT-based authentication
- Secure HS256 key generation using `SecureRandom`
- Role-based access control (RBAC)
- Request validation and sanitization
- Audit logging
- Probe and automated scan detection

### Operational
- Jenkins - CI/CD pipeline integration

### Logging

The application implements a structured, file-based logging system designed for observability, auditing, and troubleshooting in automation-heavy environments.

- **Log Levels**  
  Controlled via the `PWLOG_LEVEL` environment variable:
  - `DEBUG` → full trace logging (`Level.ALL`)
  - Any other value → defaults to `INFO`

- **Log Output & Organization**
  - Logs are written to a `PWLogs` directory in the application working directory
  - Each logger writes to a dedicated file named after its class
  - Logs are appended by default (configurable)

- **Key Capabilities**
  - Method entry/exit logging with parameter tracking
  - Automatic caller method detection
  - REST request logging (request path and client IP) for **critical endpoints**
  - Log file rotation using sequence-based file handlers

- **Format**
  - Human-readable output via Java `SimpleFormatter`
  - Includes timestamp, log level, and source class/method

To enable debug logging:

```bash
export PWLOG_LEVEL="DEBUG"
```

### 🛠️ Tech Stack

#### Core Technologies

* **Java (JDK 21)**
* **Jakarta EE 10** (platform API, provided by the application server)
* **Hibernate ORM 7**
* **MariaDB 10.6+**
* **JWT (JJWT)**

#### Key Dependencies

* **MariaDB JDBC Driver**
* **Apache Commons Lang 3**
* **JSpecify**
* **Jersey Client**
* **Jakarta JSON Processing (JSON-P)**

#### Development & Testing

* **Maven**
* **JUnit** (unit and integration testing)
* **Arquillian** (container-based integration testing)
* **ShrinkWrap Resolver** (test deployment assembly)
* **SpotBugs** (static analysis)
* **fmt-maven-plugin** (code formatting)
* **Maven WAR Plugin**

## 📋 Prerequisites

### Development
- JDK 21
- Maven 3.8+
- MariaDB 10.6+ or MySQL 8.0+
- Docker (optional)

### Application Servers
- **Primary:** OpenLiberty 23.0.0.3+
- **Other Jakarta EE 10 compatible servers:** WildFly 27+, GlassFish 7.x, supported Payara releases

*Note: Some server-specific configuration may be required for non-OpenLiberty deployments.*

## Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/Urbine/PostWizardREST.git
   cd PostWizardREST
    ```

2. Configure database access in:

   * `src/main/liberty/config/server.xml`
   * `src/main/resources/META-INF/persistence.xml`

3. Build the project

   ```bash
   mvn -DskipTests package
   ```
   > **Note**: 
   > The `-DskipTests` option is used to skip the tests during the build process. 
   > At that time in the project's lifecycle, the test suite was executed manually before every commit,
   > so that the pipeline could take care of the remote deployment. Improving that used to be
   > a milestone in the roadmap
   
4. Deploy the generated WAR to a Jakarta EE 10–compatible application server.

## API Documentation

### API Overview (OpenAPI-Style)

#### Authentication

| Method | Endpoint         | Description                           | Auth  |
| -----: | ---------------- | ------------------------------------- | ----- |
|   POST | `/v1/auth/login` | Authenticate user and issue JWT token | Basic |

---

#### Posts

| Method | Endpoint                                   | Description                            | Auth |
| -----: | ------------------------------------------ | -------------------------------------- | ---- |
|    GET | `/v1/posts/{postId}`                       | Retrieve a single post                 | JWT  |
|    GET | `/v1/posts/meta/{postId}`                  | Retrieve metadata for a post           | JWT  |
|    GET | `/v1/posts/meta/dump`                      | Retrieve metadata for all posts        | JWT  |
|    GET | `/v1/posts/dump?type={postType}`           | Retrieve posts by type                 | JWT  |
|   POST | `/v1/posts/{postId}`                       | Update post content                    | JWT  |
|   POST | `/v1/posts/batch`                          | Batch update multiple posts            | JWT  |
|   POST | `/v1/posts/meta/{postId}`                  | Update post metadata                   | JWT  |
|   POST | `/v1/posts/meta/batch`                     | Batch update post metadata             | JWT  |
|   POST | `/v1/posts/featured/randomize?limit={int}` | Randomize featured posts (e.g. videos) | JWT  |

---

#### Taxonomies

| Method | Endpoint                | Description                             | Auth |
| -----: | ----------------------- | --------------------------------------- | ---- |
|   POST | `/v1/taxonomies/check`  | Link or unlink taxonomy terms to a post | JWT  |
|   POST | `/v1/taxonomies/add`    | Create taxonomy terms                   | JWT  |
| DELETE | `/v1/taxonomies/remove` | Remove taxonomy terms                   | JWT  |

---

#### Conventions

* **Auth**:

  * `Basic` → used only for token issuance
  * `JWT` → required for all protected endpoints
* **Content-Type**: `application/json`
* **Responses**: Consistent JSON structure with structured error payloads
* **Batch endpoints**: Accept arrays of objects

---

### API in Depth

#### 🔐 Authentication

JWT-based authentication with **Basic Auth** used only to obtain the initial token.

##### Login (Get JWT)

```http
POST /v1/auth/login
Authorization: Basic base64(username:password)
Content-Type: application/json
```

```bash
# Local cURL example:
curl -X POST "http://localhost:9080/PostWizardREST/v1/auth/login" \
  -H "Authorization: Basic $(echo -n 'username:password' | base64)" \
  -H "Content-Type: application/json"
```

**Response**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "bearer",
  "expiration": "2025-10-06T16:33:01+07:00"
}
```

**Usage**

```http
Authorization: Bearer <jwt>
```

*Tokens are valid for 1 hour and must be refreshed after expiration.*

---

#### 📝 Posts

##### Read

* `GET /v1/posts/{postId}` — Get post
* `GET /v1/posts/meta/{postId}` — Get post metadata
* `GET /v1/posts/meta/dump` — Get all post metadata
* `GET /v1/posts/dump?type={postType}` — Get posts by type (`post`, `attachment`, `all`)

---

##### Update

```http
POST /v1/posts/{postId}
Content-Type: application/json
```

```json
{
  "title": "Updated Post Title",
  "content": "Updated post content..."
}
```

---

##### Metadata Update

```http
POST /v1/posts/meta/{postId}?autothumb={bool}&retries={int}&timeout={sec}
Content-Type: application/json
```

```json
{
  "thumbUrl": "http://example.com/thumb.jpg",
  "yoastFocusKw": "java",
  "yoastMetaDesc": "Yoast-compatible meta description"
}
```

---

##### Batch Operations

* `POST /v1/posts/batch` — Batch post updates
* `POST /v1/posts/meta/batch` — Batch metadata updates

```json
[
  { "postID": 1, "title": "First Post" },
  { "postID": 2, "status": "publish" }
]
```

---

##### Featured Content

```http
POST /v1/posts/featured/randomize?limit={int}
```

Randomizes featured posts (e.g. videos).

---

#### 🏷️ Taxonomies

##### Link / Unlink Terms

```http
POST /v1/taxonomies/check?id={postId}&link={bool}&unlink={bool}
Content-Type: application/json
```

```json
{
  "name": "Technology",
  "slug": "tech",
  "taxonomy": {
    "taxonomy_name": "category",
    "taxonomy_description": "This is a new category."
  }
}
```

---

##### Add Terms

```http
POST /v1/taxonomies/add?clean={bool}
Content-Type: application/json
```

```json
{
  "name": "Java 21",
  "slug": "java-21",
  "taxonomy": { "taxonomy_name": "post_tag" }
}
```

---

##### Remove Terms

```http
DELETE /v1/taxonomies/remove
Content-Type: application/json
```

```json
{
  "name": "Obsolete Term",
  "taxonomy": { "taxonomy_name": "category" }
}
```

## Project Structure

```
src/main/java/net/ygbstudio/postwizard/
├── auth/           # Authentication & authorization
├── dao/            # Data access layer
├── dto/            # Data transfer objects
├── entities/       # JPA entities
├── exceptions/     # Custom exceptions
├── filters/        # HTTP filters
├── mappers/        # Exception mappers
├── models/         # Domain models
├── rest/           # REST endpoints
├── service/        # Business logic
├── tasks/          # Scheduled tasks
└── utils/          # Utilities
```

## Code Formatting

Uses **fmt-maven-plugin**, based on the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

```bash
mvn fmt:format
```

## 📄 License

This project is licensed under the **Mozilla Public License 2.0 (MPL-2.0)**.

## ⚠️ Disclaimer

This project is provided **as-is**, without warranty of any kind, express or implied.

The author assumes no responsibility for security issues, data loss, or misuse.
Users are solely responsible for compliance with applicable laws, platform terms of service,
and content regulations when using or adapting this code.