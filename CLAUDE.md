# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a cloud-native Java blog application built on **Quarkus** with a headless CMS integration via **Contentful GraphQL**. It features reactive programming, Kubernetes deployment capabilities, and GraalVM native image support for production environments.

**Tech Stack:**
- Language: Java 25
- Framework: Quarkus (cloud-native, containerized)
- Frontend: Qute templates, Bootstrap 5, vanilla JS
- Database: PostgreSQL (Aiven Cloud)
- CMS: Contentful (GraphQL API)
- Build: Apache Maven 3
- External APIs: Google reCAPTCHA, Gravatar

## Architecture

The project uses a **layered, modular architecture** with 4 Maven modules:

```
component (REST endpoints, templates, static assets)
    ↓
service (business logic, DAOs, converters, orchestration)
    ↓
client (Contentful GraphQL, Google reCAPTCHA, HTTP clients)
    ↓
model (DTOs, domain objects, validation)
```

### Module Responsibilities

| Module | Role | Key Classes |
|--------|------|------------|
| **component** | Web presentation layer | `HomeResource`, `BlogPostResource`, `AboutResource`, `RssFeedResource` |
| **service** | Business logic & data access | `BlogPostService`, `BlogPostCommentService`, DAOs, Converters |
| **client** | External API integration | `ContentfulClient` (GraphQL), `GoogleClient` (reCAPTCHA) |
| **model** | Domain models & DTOs | `BlogPost`, `BlogPostDetail`, `BlogPostComment`, `Profile`, etc. |

### Data Flow

1. **REST Endpoints** (component) receive requests
2. **Services** (service) orchestrate business logic
3. **DAOs** (service) manage database operations
4. **Clients** (client) fetch external data (Contentful, Google)
5. **Converters** (service) transform between domain and API models
6. **Templates** (component) render HTML responses

### Key Architectural Patterns

- **Dependency Injection**: Quarkus ARC (CDI-based)
- **Converter Pattern**: Separate converters for model transformations
- **Repository Pattern**: DAO classes for data access
- **Reactive Streams**: Smallrye Mutiny for async operations

## Development Commands

### Prerequisites

```bash
# Install GraalVM JDK 25 (recommended via sdkman)
sdk install java 25-graalce
```

### Common Commands

| Command | Purpose |
|---------|---------|
| `mvn compile quarkus:dev` | **Live coding mode** with hot reload (primary dev command) |
| `mvn test` | Run unit tests only |
| `mvn verify` | Run unit tests + integration tests |
| `mvn package` | Package as JAR for production |
| `mvn package -Dnative` | Build native executable (slower, but optimal for production) |
| `mvn clean package -Dnative -DskipTests` | Fast native build, skip tests |

### Development Workflow

1. **Starting dev server**: `mvn compile quarkus:dev`
   - Runs on `http://localhost:9000`
   - Auto-reloads on code changes
   - Includes live debugging

2. **Running specific test**:
   ```bash
   # Unit test
   mvn test -Dtest=HomeResourceTest
   # Integration test
   mvn verify -Dit.test=HomeResourceIT
   ```

3. **Building for production**:
   ```bash
   # Native image (recommended for Kubernetes)
   mvn clean package -Dnative
   # OR standard JAR
   mvn package -Dquarkus.package.jar.type=uber-jar
   ```

### Publishing Native Image to Registry

```bash
mvn clean package -Dnative -DskipTests \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.push=true \
    -Dquarkus.container-image.group={group} \
    -Dquarkus.container-image.name={name} \
    -Dquarkus.container-image.tag={version} \
    -Dquarkus.container-image.registry={registry} \
    -Dquarkus.docker.dockerfile-jvm-path=src/main/docker/Dockerfile.native \
    -Dquarkus.docker.buildx.platform=linux/amd64 \
    -Dquarkus.native.builder-image=graalvm \
    -Dquarkus.native.container-build=true
```

## Required Environment Variables

Set these before running the application:

```bash
GDEVXY_DATABASE_USERNAME=<postgres-user>
GDEVXY_DATABASE_PASSWORD=<postgres-password>
GOOGLE_CAPTCHA_SECRET=<google-recaptcha-secret>
GRAVATAR_API_KEY=<gravatar-api-key>
CONTENTFUL_CDA_TOKEN=<contentful-content-delivery-api>
CONTENTFUL_CMA_TOKEN=<contentful-content-management-api>
```

## Directory Structure

```
blog/
├── component/                 # REST endpoints, templates, static assets
│   ├── src/main/java/...     # HomeResource, BlogPostResource, etc.
│   ├── src/main/resources/
│   │   ├── templates/        # Qute HTML templates (base.html, blog pages)
│   │   └── web/              # Static assets (CSS, JS, images, fonts)
│   └── src/test/             # Unit and integration tests
│
├── service/                   # Business logic & data access
│   ├── src/main/java/...
│   │   ├── service/          # BlogPostService, converters
│   │   ├── dao/              # DAOs (BlogPostDao, CommentDao, etc.)
│   │   └── model/            # JPA entities
│   ├── src/main/resources/
│   │   └── db/migration/     # Flyway migrations (V1__Create_BlogPost.sql, etc.)
│   └── src/test/             # Service layer tests
│
├── client/                    # External API clients
│   ├── src/main/java/...
│   │   ├── contentful/       # ContentfulClient (GraphQL)
│   │   ├── google/           # GoogleClient (reCAPTCHA)
│   │   └── reflection/       # GraalVM reflection config
│   └── schema.json           # Contentful GraphQL schema
│
├── model/                     # Domain models & DTOs
│   └── src/main/java/...
│       └── model/            # BlogPost, Profile, Image, etc.
│
├── .deploy/                   # Kubernetes manifests
├── doc/
│   ├── build.md              # Build instructions
│   └── cluster.md            # Kubernetes deployment docs
├── README.md                  # Project overview
└── pom.xml                    # Root Maven POM (multi-module)
```

## Database Schema

The application uses **Flyway** for database migrations. All schemas are auto-created on startup.

**Main Tables:**
- `blog_post` - Blog posts (created by Contentful migration)
- `blog_post_rating` - Post ratings/likes
- `blog_post_comment` - Comments on posts
- `blog_post_comment_reply` - Replies to comments

**Access Pattern:**
- DAOs (service/dao/*.java) handle all SQL operations
- No raw SQL queries in service layer
- JPA entities (service/model/entity/*.java) define table schemas

## Important Coding Guidelines

### Adding New Features

1. **Start in the `model` module**: Define DTOs and domain objects
2. **Add to `client` module**: If external API calls are needed (e.g., Contentful queries)
3. **Implement in `service` module**: Add service classes, DAOs, converters
4. **Expose via `component` module**: Create REST endpoints or templates

### Database Changes

1. Create a new **Flyway migration** in `service/src/main/resources/db/migration/`
2. Follow naming: `V{number}__{description}.sql` (e.g., `V5__Add_Tags_Table.sql`)
3. Migrations run automatically on `mvn compile quarkus:dev`

### Testing

- **Unit Tests**: Test individual components in isolation (use Mockito)
- **Integration Tests**: End-to-end tests marked with `IT` suffix
- **Mocking External APIs**: Use WireMock for Contentful and Google APIs
- **Test Data**: Use "Mother" pattern classes (e.g., `BlogPostMother`, `ImageMother`)

### REST Endpoints

All REST endpoints are in the `component` module as Resource classes:
- `HomeResource.java` - GET / (blog listing)
- `BlogPostResource.java` - GET /blog/{slug} (detail page)
- `AboutResource.java` - GET /about
- `RssFeedResource.java` - GET /feed.xml

Return responses wrapped in appropriate models from `model` module.

### Templates & Frontend

- **Base template**: `component/src/main/resources/templates/base.html`
- **Page templates**: Located in resource-specific directories
- **Static assets**: `component/src/main/resources/web/app/`
- **CSS**: Custom fonts (Lora, Open Sans) and Bootstrap 5
- **JS bundling**: Configured in parent pom.xml (bundles utils and blog-page modules)

## CI/CD Pipeline

GitHub Actions workflows in `.github/workflows/`:

- **build.yml**: Maven build, tests, Docker image build
- **release.yml**: Release automation
- **dependabotautomerge.yml**: Auto-merge dependency updates

## Deployment

### Kubernetes

Manifests in `.deploy/`:
- `deployment.yaml` - 2-replica pod deployment
- `service.yaml` - ClusterIP service
- `ingress.yaml` - Ingress routing rules
- `issuer.yaml` - SSL/TLS certificate management

### Docker

Multiple Dockerfile variants:
- `Dockerfile.jvm` - Standard JVM containerization
- `Dockerfile.native` - GraalVM native image (recommended)
- `Dockerfile.native-micro` - Ultra-lightweight native image

Build and deploy to Oracle Cloud Registry via Maven (see native image command above).

## External Integrations

### Contentful (Headless CMS)

- **GraphQL Client**: `client/contentful/ContentfulClient.java`
- **Query Models**: `client/contentful/model/`
- **Converters**: `service/service/contentful/converter/`
- Configuration: `client/src/resources/application.properties`
- GraphQL schema: `client/schema.json`

**Common Operations:**
- Fetch blog posts: `BlogPostService.getAll()`, `getBySlug(slug)`
- Fetch blog post details: `BlogPostService.getBySlug(slug)`
- Image handling: `ContentfulAssetService`, `ImageConverter`

### Google reCAPTCHA

- **Client**: `client/google/GoogleClient.java`
- **Service**: `service/service/captcha/CaptchaService.java`
- Validates form submissions before saving comments

### Gravatar

- Used for user avatar images in comments
- Integrated in templates via `TemplateGlobals` extension

## Troubleshooting Common Issues

### Native Image Build Fails

- Ensure GraalVM JDK 25 is installed: `java -version`
- Check `client/reflection/ThirdPartyReflectionConfiguration.java` for missing reflection configs
- Rebuild index: `mvn clean install -Dmaven.wagon.http.retryHandler.count=3`

### Contentful API Errors

- Verify `CONTENTFUL_CDA_TOKEN` and `CONTENTFUL_CMA_TOKEN` are set
- Check `client/graphql.config.yml` configuration
- Review `ContentfulClient` for query syntax

### Database Migration Issues

- Check that migrations follow naming convention: `V{number}__{description}.sql`
- Ensure migrations are idempotent (safe to run multiple times)
- View migration history in `flyway_schema_history` table

### Tests Failing

- Run `mvn clean verify` to clear old build artifacts
- Check test data builders (Mother classes) for correct test fixtures
- Use `-X` flag for debug output: `mvn test -X`

## Performance Optimization Notes

- **Caching**: Quarkus Cache is configured; add `@CacheResult` annotations to service methods
- **Async**: Use Smallrye Mutiny (`Uni`/`Multi`) for non-blocking operations
- **Native Image**: Significantly faster startup and lower memory footprint than JVM
- **Web Bundling**: JavaScript modules are bundled automatically (configured in pom.xml)

## Code Style & Conventions

- **Java Version**: Java 25 (use modern features where applicable)
- **Build Tool**: Maven (all build operations via Maven, not IDE shortcuts)
- **Dependency Injection**: Quarkus ARC - use `@Inject` for dependencies
- **Database**: JPA entities with Hibernate; use DAOs for access
- **Testing**: JUnit 5, AssertJ for assertions, Mockito for mocking
