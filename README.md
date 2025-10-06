# PostWizardREST

A high-performance, secure RESTful API for WordPress content management, built with modern Java Enterprise Architectural patterns. This API provides a robust alternative to the default WordPress REST API, offering significant performance improvements, enhanced security features, and advanced content management capabilities.

## Why Choose PostWizardREST for A WordPress Site?

- **Lightning-Fast Performance**: Built on Jakarta EE 10 with optimized database queries and on par with modern Java Development Kit (JDK) performance enhancements.
- **Enterprise-Grade Security**: Implements JWT authentication, role-based access control, and request validation to protect your content.
- **Advanced Content Management**: Batch operations, comprehensive taxonomy support, and flexible querying options for efficient content handling.
- **Universal Integration**: Connect PostWizardREST with any content pipeline or automation tool, regardless of the programming language.  
- **Full WordPress Compatibility**: Seamlessly works with themes and plugins that rely on custom fields and metadata, ensuring complete compatibility with an existing WordPress ecosystem.
- **Headless CMS Ready**: Perfect for headless WordPress architectures, and mobile apps that require fast, reliable content delivery.
- **Developer Friendly**: Clean, well-documented API endpoints with consistent response formats and comprehensive error handling.
- **Scalable Architecture**: Designed to handle high traffic loads while maintaining performance, making it ideal for high-traffic WordPress sites.
- **Modern Development Workflow**: Built with modern development practices, including comprehensive integration testing, CI/CD integration, and containerization support.

This API extends WordPress's capabilities while maintaining compatibility with your existing content structure, making it an excellent choice for developers looking to build high-performance applications on top of WordPress.

## ✨ Features

### Core Features
- **Content Management**: Full CRUD operations for blog posts
- **Advanced Taxonomy System**: Hierarchical categorization and tagging system
- **Metadata Management**: Extensible metadata system for custom post attributes
- **Batch Processing**: Efficient bulk operations for posts and metadata
- **Scheduled Tasks**: Automated content management tasks

### Security Features
- JWT-based authentication
- Built-in generation of HS256 (HMAC-SHA256) keys for JWT signing using Java's SecureRandom
- Role-based access control (RBAC)
- Request validation and sanitization
- Comprehensive audit logging
- Probe detection support: Blocks automated scanning and probing attempts 

## 🛠️ Tech Stack

### Core Technologies
- **Java - JDK 21** - Modern Java platform
- **Jakarta EE 10** - Enterprise-grade Java platform
- **Hibernate ORM 7.0.4** - Robust object-relational mapping
- **MariaDB 10.6+** - High-performance relational database (via MariaDB Java Client 3.5.3)
- **JWT Authentication** - Secure API authentication using jjwt 0.12.6

### Key Dependencies
- **Apache Commons Lang 3.18.0** - Utility functions and helpers
- **JSpecify 1.0.0** - Nullness and type annotations
- **Jakarta JSON Processing 2.0.1** - JSON processing API
- **Jersey Client 4.0.0** - REST client for testing

### Development Tools
- **Maven** - Build automation and dependency management
- **JUnit 6** - Unit testing framework
- **Arquillian 1.10.0** - Integration testing
- **SpotBugs** - Static code analysis
- **fmt-maven-plugin** - Code formatting (See the [Code Formatting](#code-formatting) section)
- **Maven War Plugin 3.3.2** - Web application packaging

### Runtime Environment
- **OpenLiberty** - Application server (testing with Arquillian Liberty)
- **Jakarta JSON Processing** - JSON handling
- **Jakarta Servlet 6.0** - Web container API

### Security
- **JWT (0.12.6)** - Secure authentication
- **Jakarta Security** - Authentication and authorization
- **Jakarta Validation** - Request validation

### Operational
- **Docker** - Containerization support
- **Jenkins** - CI/CD pipeline integration

### Logging

The application features a robust logging system with the following characteristics:

- **Log Levels**: Controlled by the `PWLOG_LEVEL` environment variable
  - `DEBUG`: Enables detailed logging (Level.ALL)
  - Any other value: Defaults to INFO level logging

- **Log Files**:
  - Logs are stored in a `PWLogs` directory in the application's working directory
  - Each logger creates its own log file named after the logger's class name
  - Logs can be configured to append to existing files or overwrite them (Append mode is enabled by default)

- **Key Logging Features**:
  - Method entry/exit logging with parameter tracking
  - Automatic caller method detection
  - Request path and IP address logging for REST endpoints

- **Log File Rotation**:
  - Supports log file rotation with sequence numbers
  - Configurable through the FileHandler pattern

- **Log Format**:
  - Uses Java's SimpleFormatter for human-readable output
  - Includes timestamps, log levels, and source class/method information

To enable debug logging, set the environment variable before starting the application:
```bash
export PWLOG_LEVEL="DEBUG"
```

## 📋 Prerequisites

### Development Environment
- JDK 21
- Maven 3.8+
- MariaDB 10.6+ or compatible MySQL
- Docker (optional, for containerized deployment)

### Application Server

**Primary Development & Deployment Target:**
- ✅ **OpenLiberty 23.0.0.3+** - Primary supported application server with optimized configuration

**Also Compatible With Any Jakarta EE 10 Compatible Application Server:**
- Payara 7.x
- WildFly 27+
- GlassFish 7.0+

*Note: While the application is developed and tested primarily on OpenLiberty, it's built on standard Jakarta EE 10 APIs and should work on any compatible application server. Some server-specific configuration may be required for non-OpenLiberty deployments.*

### Database
- MariaDB 10.6+ (recommended)
- MySQL 8.0+ (compatible)
- Database configuration required in `src/main/liberty/config/server.xml`

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/Urbine/PostWizardREST.git
   cd PostWizardREST
   ```
   
2. **Configure Database**
   - **Prerequisite**: A working WordPress installation is required as the application integrates with an existing WordPress database
   - Update the database connection details and JNDI datasource name in `src/main/liberty/config/server.xml` to point to your WordPress database
   - Verify the JNDI datasource name in `src/main/liberty/config/server.xml` matches the JNDI datasource name in `src/main/resources/META-INF/persistence.xml`
   
3. **Test and build the project**
   ```bash
   # Set up Liberty server and install required features
   mvn liberty:create liberty:install-feature
   
   # Configure Arquillian for integration testing
   mvn liberty:configure-arquillian
   
   # Test and Build the project
   mvn package
   ```

4. **Deploy**
   Deploy the generated `PostWizardREST.war` to your application server

## API Documentation

### Authentication

The application uses JWT (JSON Web Tokens) for authentication with Basic Auth for the initial token request.

#### Login to get JWT Token

```http
POST /v1/auth/login
Authorization: Basic base64(username:password)
Content-Type: application/json
```
```bash
# Example using curl:
curl -X POST "http://localhost:9080/PostWizardREST/v1/auth/login" \
      -H "Authorization: Basic $(echo -n 'username:password' | base64)" \
      -H "Content-Type: application/json"
```

#### Response
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "bearer",
  "expiration": "2025-10-06T16:33:01+07:00"
}
```

#### Using the JWT Token

Include the token in subsequent requests using the `Authorization` header:

```http
GET /v1/posts/protected-endpoint
Authorization: Bearer your.jwt.token.here
```

*Note: The token is valid for 1 hour. After expiration, you will need to re-authenticate.*

### Posts

#### Endpoints

- `GET /v1/posts/meta/{postId}` - Get post metadata
- `GET /v1/posts/meta/dump` - Get all post metadata
- `GET /v1/posts/{postId}` - Get a specific post

#### Payload Examples

##### Query Parameters

- `postId`: Numeric ID of the post to retrieve or update
- `postType`: Type of posts to retrieve (e.g., "post", "attachment", "photos", "all")
- `limit`: Maximum number of videos to select (default: 10)
- `link`: (Optional, boolean) Set to `true` to automatically link the term to the post
- `unlink`: (Optional, boolean) Set to `true` to unlink the term from the post

---

- `POST /v1/posts/{postId}` - Update a post

```http
POST /v1/posts/123
Content-Type: application/json
```
```json
{
  "title": "Updated Post Title",
  "content": "Updated post content..."
}
```
---
- `POST /v1/posts/meta/{postId}` - Update post metadata

```http
POST /v1/posts/meta/123
Content-Type: application/json
```
```json
{
  "thumbUrl": "http://example.com/thumb.jpg",
  "yoastFocusKw": "java",
  "yoastMetaDesc": "This is a meta description compatible with the Yoast SEO plugin"
}
```
---
- `POST /v1/posts/batch` - Batch update multiple posts

```http
POST /v1/posts/batch
Content-Type: application/json
```
```json
[
  {
    "postID": 1,
    "title": "First Post"
  },
  {
    "postID": 2,
    "status": "publish"
  }
]
```
---
- `POST /v1/posts/meta/batch` - Batch update post metadata

```http
POST /v1/posts/meta/batch
Content-Type: application/json
```
```json
[
  {
    "postID": 1,
    "hd": true
  },
  {
    "postID": 2,
    "videoURL": "http://example.com/video.mp4"
  }
]
```
---
- `GET /v1/posts/dump?type={postType}` - Get posts by type

```http
GET /v1/posts/dump?type=post
GET /v1/posts/dump?type=attachment
GET /v1/posts/dump?type=all
```
---
- `POST /v1/posts/featured/randomize?limit={limit}` - Randomize featured videos

```http
POST /v1/posts/featured/randomize?limit=10
```

#### Taxonomy Support

The application provides comprehensive support for managing post taxonomies (categories, tags, etc.) through the following endpoints:

##### Link/Unlink Terms to Posts

```http
POST /v1/posts/taxonomies?postId={postId}&link={link}&unlink={unlink}
Content-Type: application/json
```
```json
{
  "name": "Technology",
  "slug": "tech",
  "taxonomy": {
    "taxonomy_name": "category",
    "taxonomy_description" : "This is a new category."
  }
}
```

##### Remove Terms

```http
DELETE /v1/posts/taxonomies/remove
Content-Type: application/json
```
```json
{
  "name": "Obsolete Term",
  "taxonomy": {
    "taxonomy_name": "category"
  }
}
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/main/java/net/ygbstudio/postwizard/
├── auth/           # Authentication and authorization
├── dao/            # Data Access Objects
├── dto/            # Data Transfer Objects
├── entities/       # JPA Entities
├── exceptions/     # Custom exceptions
├── filters/        # HTTP filters
├── mappers/        # Exception mappers
├── models/         # Business models
├── rest/           # REST endpoints
├── service/        # Business logic
├── tasks/          # Scheduled tasks
└── utils/          # Utility classes
```

## Code Formatting

The project uses [fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin) to maintain consistent code style. The plugin automatically formats Java code according to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

- **Usage**:
  - Run `mvn fmt:format` to format all code
  - Formatter settings are defined in the project's [pom.xml](cci:7://file:///home/ygabriel/WMJakartaEE/PostWizardREST/pom.xml:0:0-0:0)

- **Configuration**:
  - Based on Google Java Format Style
  - Integrated with Maven build lifecycle
  - Customizable through [pom.xml](cci:7://file:///home/ygabriel/WMJakartaEE/PostWizardREST/pom.xml:0:0-0:0)

## 🚀 Roadmap

PostWizard is under active development with an exciting roadmap of upcoming features and improvements:

### 🚧 In Development
- **Rate Limiting**: Protect API endpoints from abuse with configurable rate limits
- **Post Dump Pagination**: Efficiently handle large datasets with cursor-based pagination
- **Admin Notifications**: Real-time alerts for system events and API activities
- **PostWizard SDK for Python**: Client library for Python applications

### ⏳ Planned Features
- **Advanced Telemetry**: Detailed API usage analytics and performance metrics
- **Comprehensive Reporting**: Generate and export detailed content and usage reports
- **AI Integration**: Support for Model Context Protocol (MCP) Clients for AI Workflows
- **GraphQL Endpoint**: Alternative to REST with flexible query capabilities
- **Media Management**: Enhanced handling of media uploads and processing
- **User Management**: Extended user profile and permission controls

### 🛠️ Developer Experience
- **OpenAPI Documentation**: Interactive API documentation
- **SDK Generation**: Client libraries for popular programming languages
- **Enhanced Logging**: Structured logging for better debugging
- **Performance Profiling**: Built-in tools for performance analysis

### 🔍 Future Considerations
- **Multi-site Support**: Manage multiple WordPress installations from a single API
- **Content Versioning**: Track and revert to previous content versions
- **Scheduled Publishing**: Advanced scheduling capabilities
- **Webhook Support**: Real-time event notifications for content changes
- **Multi-language Support**: Built-in internationalization and localization
- **Plugin Ecosystem**: Extend functionality through custom plugins

### 🛡️ Security Enhancement Proposals
- **OAuth 2.0 Support**: Additional authentication options
- **IP Whitelisting**: Fine-grained access control
- **Audit Logging**: Comprehensive security event tracking

---

>**License Notice**
>
>Copyright © 2025 Yoham Gabriel @ [YGBStudio](https://ygbstudio.net)
>
>Permission is granted to **view and read** this code for the purposes allowed by the copyright holder.  
>No permission is granted to **copy**, **modify**, **distribute**, or **use** this code, in whole or in part,  
>for any other purpose without explicit written consent from the copyright holder.
