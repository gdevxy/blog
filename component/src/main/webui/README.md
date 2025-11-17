# gdevxy Blog - React Frontend

This is the React/TypeScript SPA frontend for the gdevxy Blog application, built with Vite and integrated via Quarkus Quinoa.

## Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable React components
│   │   ├── Layout.tsx       # Main layout component with navigation
│   │   └── Layout.css       # Layout styling
│   ├── hooks/               # Custom React hooks
│   │   ├── useBlogPosts.ts  # Hook for fetching paginated blog posts
│   │   ├── useBlogPost.ts   # Hook for fetching single blog post
│   │   ├── useProfile.ts    # Hook for fetching user profile
│   │   └── index.ts         # Hooks barrel export
│   ├── pages/               # Page components
│   │   ├── HomePage.tsx     # Home/hero page
│   │   ├── BlogListPage.tsx # Blog articles listing with pagination
│   │   ├── BlogDetailPage.tsx # Single blog post detail
│   │   └── AboutPage.tsx    # User profile/about page
│   ├── services/            # API client and utilities
│   │   └── api.ts           # Axios-based API client singleton
│   ├── types/               # TypeScript type definitions
│   │   └── api.ts           # API response/request types
│   ├── App.tsx              # Root app component with routing
│   ├── main.tsx             # Application entry point
│   └── index.css            # Global styles
├── index.html               # HTML entry point
├── package.json             # Dependencies and scripts
├── tsconfig.json            # TypeScript configuration
├── tsconfig.node.json       # TypeScript config for Vite
├── vite.config.ts           # Vite build configuration
└── .gitignore               # Git ignore rules
```

## Setup Instructions

### 1. Prerequisites

- Node.js 18+ and npm 9+ (or equivalent)
- Java 25+ (for the Quarkus backend)
- Maven 3.9+

### 2. Installation

Install frontend dependencies:

```bash
cd frontend
npm install
```

### 3. Development Mode

Start the development server with hot module replacement:

```bash
npm run dev
```

The Vite dev server will start on `http://localhost:3000`

**From the backend perspective**, when running Quarkus in dev mode:

```bash
mvn quarkus:dev -pl component
```

Quarkus will:
- Start the backend API on `http://localhost:9000`
- Launch Quinoa, which starts the Vite dev server on port 3000
- Proxy frontend requests from the Quarkus app through Quinoa
- Enable hot reload for both backend and frontend

### 4. Build

Build the frontend for production:

```bash
npm run build
```

This creates an optimized build in the `dist/` directory.

Quarkus/Quinoa will automatically:
- Build the React app when running `mvn clean package`
- Include the built frontend in the JAR/native image
- Serve it from the root path (`/`)

## API Integration

### Axios Client

The `src/services/api.ts` file provides a singleton Axios instance configured with:

- **Base URL**: `/api/v1` (or `VITE_API_URL` env var)
- **Timeout**: 30 seconds
- **Error Handling**: Automatic error logging with error ID
- **Headers**: Content-Type: application/json

### Custom Hooks

Three custom hooks simplify data fetching:

#### `useBlogPosts(initialPage?: number, pageSize?: number)`

Fetches paginated blog posts with pagination controls:

```typescript
const {
  posts,
  loading,
  error,
  currentPage,
  hasNextPage,
  goToPage,
  nextPage,
  previousPage,
  filterByTag
} = useBlogPosts(0, 10);
```

#### `useBlogPost(slug: string, preview?: boolean)`

Fetches a single blog post by slug:

```typescript
const { post, loading, error } = useBlogPost('my-article', false);
```

#### `useProfile()`

Fetches user profile information:

```typescript
const { profile, loading, error } = useProfile();
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/blog-posts` | Get paginated blog posts (supports tag filtering) |
| GET | `/api/v1/blog-posts/recent` | Get recent posts for home page |
| GET | `/api/v1/blog-posts/{slug}` | Get blog post detail |
| GET | `/api/v1/blog-posts/{slug}/comments` | Get comments for a post |
| POST | `/api/v1/blog-posts/{slug}/comments` | Add comment to post |
| GET | `/api/v1/blog-posts/feed.xml` | Get RSS feed |
| GET | `/api/v1/profile` | Get user profile |

## TypeScript Types

All API response types are defined in `src/types/api.ts`:

- `BlogPostsResponseDto` - Paginated response wrapper
- `BlogPostDetailDto` - Complete post with content
- `BlogPostSummaryDto` - Post summary for listings
- `BlogPostComment` - Comment with nested replies
- `ProfileDto` - User profile information
- `ErrorResponse` - API error format

## Styling

The application uses vanilla CSS with:

- **CSS Variables** for theming (in `index.css`)
- **Responsive Design** with media queries (mobile-first)
- **Component-scoped CSS** (each component has its own `.css` file)

### Color Scheme

- Primary: `#646cff` (blue)
- Accent: `#535bf2` (darker blue)
- Background: `#242424` (dark)
- Text: `rgba(255, 255, 255, 0.87)` (light)

## Routing

The application uses React Router v6 with the following routes:

| Path | Component | Description |
|------|-----------|-------------|
| `/` | HomePage | Home page with recent articles |
| `/blog` | BlogListPage | All articles with pagination |
| `/blog/:slug` | BlogDetailPage | Single article detail |
| `/about` | AboutPage | User profile/about page |

## Environment Variables

Create a `.env` file in the frontend directory:

```env
VITE_API_URL=http://localhost:9000/api/v1
```

## Linting

Run ESLint:

```bash
npm run lint
```

## Build Configuration

### Vite Config Features

- **Path Aliases**: `@` maps to `src/`, `@components` to `src/components`, etc.
- **Dev Server Proxy**: Routes `/api/*` to backend API
- **Code Splitting**: Separate vendor chunks for better caching
- **Source Maps**: Disabled in production for smaller builds

### Output Structure

```
dist/
├── index.html
├── assets/
│   ├── index-[hash].js      # App code
│   ├── vendor-[hash].js     # React, Router, Axios
│   └── styles-[hash].css    # Global styles
└── favicon.svg
```

## Performance Optimization

- **Lazy Loading**: React components can be dynamically imported
- **Code Splitting**: Vendor bundle separated for better caching
- **CSS Optimization**: Unused styles removed in production builds
- **Asset Optimization**: Images and fonts optimized

## Browser Support

- Modern browsers (Chrome, Firefox, Safari, Edge)
- ES2020+ JavaScript syntax

## Integration with Quarkus

When running the full stack:

```bash
# Backend only (uses web-bundler)
mvn quarkus:dev -pl component

# With Quinoa (recommended for React SPA)
# Requires npm install in frontend/
mvn quarkus:dev -pl component
```

Quinoa will:
1. Detect the Vite project
2. Install npm dependencies
3. Start the dev server with hot reload
4. Proxy requests between frontend and backend

## Troubleshooting

### Port 3000 Already in Use

Change the port in `vite.config.ts`:

```typescript
server: {
  port: 3001,  // Change this
}
```

### API Requests Failing

1. Check that the backend is running on `http://localhost:9000`
2. Verify `VITE_API_URL` environment variable
3. Check browser console for CORS errors

### Node Modules Missing

Run `npm install` in the frontend directory

### Vite Build Issues

Clear the build cache:

```bash
rm -rf dist/
npm run build
```

## Next Steps

- Add unit tests with Vitest
- Add E2E tests with Playwright
- Implement state management (React Context, Zustand, Redux)
- Add dark mode toggle
- Implement comment system
- Add like/rating functionality
