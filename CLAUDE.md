# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a cloud-native Java blog application built on **Quarkus** with a headless CMS integration via **Contentful GraphQL**. It features reactive programming, Kubernetes deployment capabilities, and GraalVM native image support for production environments.

**Tech Stack:**
- Language: Java 25
- Framework: Quarkus (cloud-native, containerized)
- Frontend: React 18 (SPA), Bootstrap 5, TypeScript, Vite
- Frontend Dependencies: React Router, Axios, date-fns, highlight.js
- Database: PostgreSQL (Aiven Cloud)
- CMS: Contentful (GraphQL API)
- Build: Apache Maven 3 (backend), Vite (frontend)
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

## Frontend Development (React/Vite)

The frontend is a React SPA located in `component/src/main/webui/`. Node.js and npm are **required for local development**.

### Frontend Setup

**Important**: Node.js and npm are installed in `./component/.quinoa/node/` directory. This is a local installation managed by the build system.

To run frontend commands, add the .quinoa node directory to your PATH and use npm as usual:

```bash
# Add to PATH (from the project root directory)
export PATH="$PWD/component/.quinoa/node:$PATH"

# Then use npm normally
npm run dev
```

### Frontend Commands

Run these commands from `component/src/main/webui/` directory (after adding .quinoa/node to PATH):

| Command | Purpose |
|---------|---------|
| `npm run dev` | Start Vite dev server (HMR enabled, runs on port 5173) |
| `npm run build` | Build optimized production bundle |
| `npm run preview` | Preview production build locally |
| `npm run lint` | Run ESLint on TypeScript files |
| `npm run test:e2e` | Run Playwright E2E tests (requires backend Quarkus server on 9000) |
| `npm run test:e2e:ui` | Run Playwright tests with UI (requires backend Quarkus server on 9000) |

**E2E Testing Prerequisites:**
- Backend Quarkus server must be running on `http://localhost:9000`
- Run `mvn compile quarkus:dev` from project root in a separate terminal
- Then run E2E tests from `component/src/main/webui/` directory
- The Playwright config will automatically detect the running backend and use it for tests
- In CI/CD environments, the config will start its own dev server

### Frontend Architecture

**Key Frontend Technologies:**
- **React 18**: UI library
- **Vite**: Fast bundler and dev server
- **TypeScript**: Static typing for React components
- **React Router**: Client-side routing (pages: HomePage, BlogListPage, BlogDetailPage, AboutPage)
- **React Bootstrap**: UI components (Cards, Spinners, Alerts, Pagination, Navbar)
- **date-fns**: Date formatting and manipulation
- **Axios**: HTTP client for API requests

**Frontend Pages:**
- `src/pages/HomePage.tsx` - Hero section with horizontal scrolling cards
- `src/pages/BlogListPage.tsx` - Grid layout (3 columns) with blog posts and horizontal scrollable tags
- `src/pages/BlogDetailPage.tsx` - Full blog post detail with content blocks
- `src/pages/AboutPage.tsx` - About page information

**Custom Hooks:**
- `src/hooks/useBlogPosts.ts` - Fetch and paginate blog posts from API
- `src/hooks/useBlogPost.ts` - Fetch single blog post by slug

**Styling:**
- Global theme: `src/index.css` (CSS variables for dark/light mode)
- Layout: `src/components/Layout.tsx` with Bootstrap Navbar
- Bootstrap 5 imported in `src/main.tsx` AFTER custom CSS to allow theme variable overrides
- Page-specific CSS files alongside components

**Important E2E Tests:**
- `e2e/blog-list-tags-scroll.spec.ts` - Tests horizontal scroll on overflowing tag badges in blog list cards

### Frontend Component Refactoring

All frontend components have been refactored to use **Bootstrap 5 components** for consistency:
- **Layout**: Bootstrap Navbar with responsive navigation
- **HomePage**: Bootstrap Cards with horizontal scroll slider
- **BlogListPage**: Bootstrap Cards in responsive grid (3 columns on desktop), horizontal scrollable tags with wheel event support
- **BlogDetailPage**: Blockquote for description, Bootstrap badges for tags
- **AboutPage**: Bootstrap Row/Col for responsive layout

**Tags Horizontal Scroll Feature:**
The tags container on BlogListPage cards supports mouse wheel scrolling:
- When hovering over a tags container with overflowing badges, scrolling the mouse wheel will scroll the tags horizontally
- Implementation: Wheel event listener converts vertical scroll (deltaY) to horizontal scroll (scrollLeft)
- Prevents default page scroll when mouse is over the tags container

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
├── component/                 # REST endpoints and React SPA
│   ├── src/main/java/...     # HomeResource, BlogPostResource, etc.
│   ├── src/main/resources/web/   # Static assets (CSS, JS, images, fonts)
│   ├── src/main/webui/       # React SPA (Single Page Application)
│   │   ├── src/
│   │   │   ├── pages/        # Page components (BlogDetailPage, HomePage, etc.)
│   │   │   ├── components/   # Reusable components (ContentBlockRenderer, ThemeToggle, etc.)
│   │   │   ├── hooks/        # Custom React hooks (useBlogPost, etc.)
│   │   │   ├── types/        # TypeScript interfaces (api.ts - BlogPost, BlogPostDetail, etc.)
│   │   │   ├── App.tsx       # Root React component
│   │   │   ├── main.tsx      # React entry point (imports Bootstrap CSS)
│   │   │   └── index.css     # Global styles (dark/light theme)
│   │   ├── package.json      # Frontend dependencies (React, Bootstrap, date-fns, etc.)
│   │   └── vite.config.ts    # Vite bundler configuration
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

**Resource classes are thin adapters that:**
- Accept requests and extract parameters
- Delegate to service layer for business logic
- Return domain models directly (not `Response` objects)
- Never include error handling or manual HTTP status codes

**Return types:**
- Return domain models directly: `Uni<BlogPostDetail>`, `Uni<Page<BlogPost>>`, etc.
- Let `GlobalExceptionMapper` handle all exceptions
- Empty/null results are automatically interpreted as 404 by the framework

**Example pattern:**
```java
@GET
@Path("/{slug}")
public Uni<BlogPostDetail> findBlogPost(
    @PathParam("slug") String slug,
    @QueryParam("previewToken") String previewToken,
    @CookieParam("userId") String userId) {

    return blogPostService.findBlogPost(userId, previewToken, slug);
}
```

**All REST endpoints are in the `component` module as Resource classes:**
- `BlogPostsResource.java` - Blog post endpoints (`/api/v1/blog-posts`)
- Other resources follow the same pattern

**Error Handling:**
- Services throw specific exceptions (`NotFoundException`, validation errors, etc.)
- `GlobalExceptionMapper` catches all exceptions and returns appropriate HTTP responses
- Never use `Response.status()`, `onFailure()`, or manual error recovery in Resources

**Logging:**
- Minimize logging in Resource classes; it's not necessary for every endpoint
- Add logging only when debugging complex flows or tracking business-critical events
- Avoid verbose logging statements; let the application run silently unless there's a problem

### React SPA Frontend

Located in `component/src/main/webui/src/`:

**Key Files & Directories:**
- **`pages/`** - Top-level page components:
  - `BlogDetailPage.tsx` - Blog post detail view with comments, tags (Bootstrap badges), and formatted dates
  - `HomePage.tsx` - Blog listing page
  - Other page components for different routes

- **`components/`** - Reusable UI components:
  - `ContentBlockRenderer.tsx` - Renders rich text blocks from Contentful
  - `ThemeToggle.tsx` - Light/dark theme toggle
  - Layout and other shared components

- **`hooks/`** - Custom React hooks:
  - `useBlogPost()` - Fetch individual blog post with comments
  - Other data-fetching hooks

- **`types/api.ts`** - TypeScript interfaces for API responses:
  - `BlogPost` - Summary view of blog post
  - `BlogPostDetail` - Full blog post with content blocks and comments
  - `BlogPostTag` - Tag structure (`value`, `code`) - displayed as Bootstrap badges
  - `BlogPostComment` - Comment structure with nested replies

- **`index.css`** - Global styles:
  - Dark and light theme definitions using CSS variables
  - Supports system preference detection and manual `data-theme` attribute

**Frontend Dependencies:**
- **React 18** - UI library
- **React Router** - Client-side routing
- **React Bootstrap** - Bootstrap 5 components as React components (Card, Navbar, Pagination, Alert, Spinner, Row, Col, Container)
- **Bootstrap 5** - CSS framework (imported in `main.tsx` before custom CSS)
- **date-fns** - Date formatting (e.g., `format(date, 'yyyy-MM-dd \'at\' h:mm a')`)
- **highlight.js** - Code syntax highlighting
- **Axios** - HTTP client for API calls
- **Vite** - Build tool and dev server

**Frontend Development:**
```bash
cd component/src/main/webui
npm install          # Install dependencies
npm run dev          # Start Vite dev server (typically http://localhost:5173)
npm run build        # Build for production
npm run lint         # Run ESLint
npm run test:e2e     # Run Playwright E2E tests
```

**Bootstrap Component Usage:**
- **Layout (Navbar)**: Uses `react-bootstrap` `Navbar`, `Nav`, `Container` components with custom styling
- **BlogListPage**: Uses `Card`, `Pagination`, `Alert`, `Spinner` components; dates formatted with date-fns
- **AboutPage**: Uses `Row`, `Col`, `Alert`, `Spinner` for responsive layout; blockquote for description
- **BlogDetailPage**: Blockquote styling for post description with custom CSS override
- All cards use theme-aware custom CSS (background: `var(--bg-secondary)`, border: `var(--border-color)`)
- Tags/Badges: `className="badge text-bg-primary"` (Bootstrap 5 convention)

**Important Implementation Notes:**
- API calls use `/api/v1/*` endpoints (backend REST API)
- TypeScript interfaces in `types/api.ts` must match backend API response structure
- Tags structure: `{value: string, code: string}` (display as Bootstrap badges with `text-bg-primary`)
- Dates are ISO 8601 strings from backend, formatted client-side using date-fns
- Filter tags by `tag.value` to avoid rendering empty tags
- Use `key={tag.code}` for React list keys
- Bootstrap CSS utilities: `text-center`, `py-5`, `mt-2`, `ms-auto`, `gap-3`, `d-flex`, `justify-content-between`, etc.
- Custom CSS import order: `index.css` then `bootstrap.min.css` so custom theme variables override Bootstrap defaults
- Use CSS variables for all colors: `var(--bg-primary)`, `var(--text-primary)`, `var(--accent-primary)`, `var(--border-color)`

## CI/CD Pipeline

GitHub Actions workflows in `.github/workflows/`:

- **build.yml**: Maven build, tests, Docker image build
- **release.yml**: Release automation
- **dependabotautomerge.yml**: Auto-merge dependency updates

## Deployment

### Kubernetes

Deployed on OCI infrastructure on a Oracle Linux 8.10

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

### Core Principles

**No Comments Allowed** - Code must be self-explanatory through clear naming, small focused methods, and established design patterns. If you feel the need to write a comment, refactor the code instead.

**Domain-Driven Design (DDD)** - Organize code around business capabilities and domains:
- Package structure reflects business domains, not layers (e.g., `contentful.blogpost`, not `contentful.service`)
- Use ubiquitous language from domain experts in class and method names
- Place domain logic in domain objects, not anemic DTOs
- Separations of concerns: converters transform between domains, services orchestrate domain logic

**Clean Code Principles**:
- Single Responsibility Principle (SRP) - Each class and method has one reason to change
- Methods should be small (typically < 15 lines)
- Return early to reduce nesting depth
- Extract meaningful methods rather than reusing generic helper methods
- Use proper abstractions that reflect business intent

### Java & Language Standards

- **Java Version**: Java 25 (use modern features: records, sealed classes, pattern matching where applicable)
- **Naming**: Full words, avoid abbreviations except for universally understood ones (e.g., `id`, `url`)
  - Methods: Verb-noun pattern (`extractPaginationHeaders`, `parseHeader`, `validateOffset`)
  - Variables: Descriptive and domain-aware (`pageOffset` not `pOffset`, `blogPost` not `bp`)
  - Constants: UPPER_SNAKE_CASE (`MAX_SIZE`, `DEFAULT_PAGE_OFFSET`)
  - Classes: PascalCase, noun-based reflecting what they are (`PaginationFilter`, `BlogPostService`)
  - Booleans: Prefix with `is`, `has`, `can`, `should` (`isValid`, `hasContent`, `shouldRetry`)

- **Build Tool**: Maven (all build operations via Maven, not IDE shortcuts)
- **Dependency Injection**: Quarkus ARC - use `@Inject` for dependencies; prefer constructor injection for clarity
- **Database**: JPA entities with Hibernate; use DAOs for all data access; no raw SQL in service layer
- **Testing**: JUnit 5, AssertJ for assertions, Mockito for mocking

### Code Organization

**Method Organization Within a Class**:
1. Static fields and constants
2. Instance fields
3. Constructors
4. Public methods (in logical grouping)
5. Private/helper methods

**Package Naming Conventions**:
- Domain packages: `com.gdevxy.blog.{module}.{domain}`
  - Example: `service.contentful.blogpost` for blog post domain logic
  - Example: `component.pagination` for pagination cross-cutting concern
- Service classes: `{DomainName}Service` (e.g., `BlogPostService`, `CaptchaService`)
- Data access: `{DomainName}Dao` (e.g., `BlogPostDao`, `CommentDao`)
- Converters: `{SourceName}To{TargetName}Converter` or package in `converter` subdirectory
- DTOs and models: `model` or `dto` subdirectories with clear purpose names

### Method Design Patterns

**Keep Methods Focused**:
```
✓ extractPaginationHeaders(RoutingContext) - Single responsibility
✗ processRequest(RoutingContext) - Too broad, multiple concerns
```

**Use Meaningful Variable Names**:
```
✓ int pageOffset = parseHeader(ctx, "X-Page-Offset", DEFAULT_PAGE_OFFSET)
✗ int p = parseHeader(ctx, "X-Page-Offset", DEFAULT_PAGE_OFFSET)
```

**Extract Complex Logic**:
```
✓ if (pageSize > MAX_SIZE) pageSize = MAX_SIZE;  // Self-explanatory
✓ PaginationContext.set(pagination);  // Clear intent via method name
✗ ctx.put("pagination", new Pagination(o, s));  // Generic, unclear purpose
```

**Return Early, Avoid Deep Nesting**:
```
✓ if (headerValue == null || headerValue.isBlank()) return defaultValue;
  return Integer.parseInt(headerValue);

✗ if (headerValue != null && !headerValue.isBlank()) {
    try {
      return Integer.parseInt(headerValue);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  } else {
    return defaultValue;
  }
```

### Handling ThreadLocal and Resources

**Always Clean Up ThreadLocal**:
- When using `ThreadLocal` (e.g., `PaginationContext`), provide cleanup mechanisms
- Call `clear()` methods in filters or handlers after processing
- For Quarkus filters, use try-finally or response end handlers to guarantee cleanup
- This prevents memory leaks and context bleeding in thread pools and virtual threads

**Example Pattern**:
```java
ctx.addEndHandler(routingContext -> PaginationContext.clear());
```

### Exception Handling

- Use specific exception types; avoid generic `Exception`
- Fail fast and validate input at entry points
- Use meaningful exception messages that indicate what went wrong and context
- Re-throw with context preservation when appropriate
- Handle framework-level exceptions (e.g., NumberFormatException) gracefully with sensible defaults

### Immutability & Data Objects

- Use records for immutable DTOs and domain objects when possible (Java 25+)
- Use Lombok `@Getter` and `@Builder` for clarity when records aren't suitable
- Avoid mutable fields in DTOs; prefer builder pattern for construction
- Domain objects should enforce business rules in constructors/factory methods

### Testing Standards

- **Unit Tests**: Test one behavior per test; use descriptive test names (`testExtractPaginationHeadersWithValidOffset`)
- **Integration Tests**: End-to-end flows; suffix with `IT`
- **Test Naming**: Follow pattern `test{MethodName}{Scenario}{Expected}`
- **Test Data Builders**: Use "Mother" pattern classes (e.g., `BlogPostMother`, `ImageMother`) for consistent test fixtures
- **Assertions**: Use AssertJ for fluent, readable assertions
- **Mocking**: Use Mockito for external dependencies (Contentful API, Google API)
- **No Test Comments**: Test code should also be self-explanatory; test name and assertions should make intent clear
