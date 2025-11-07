import axios, { AxiosInstance } from 'axios';
import {
  BlogPostsResponseDto,
  BlogPostDetailDto,
  BlogPostSummaryDto,
  BlogPostCommentsResponseDto,
  BlogPostCommentDto,
  ProfileDto,
  AddCommentRequest,
  ErrorResponse,
} from '@types/api';

const BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

class BlogApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.data) {
          const errorData: ErrorResponse = error.response.data;
          console.error(`API Error [${errorData.errorId}]: ${errorData.message}`);
        }
        return Promise.reject(error);
      }
    );
  }

  // Blog Posts endpoints

  /**
   * Get paginated list of blog posts
   * @param page Page number (0-indexed)
   * @param limit Number of posts per page
   * @param tag Optional tag filter (comma-separated)
   */
  async getBlogPosts(
    page: number = 0,
    limit: number = 10,
    tag?: string
  ): Promise<BlogPostsResponseDto> {
    const params: Record<string, any> = { page, limit };
    if (tag) {
      params.tag = tag;
    }
    const response = await this.client.get('/blog-posts', { params });
    return response.data;
  }

  /**
   * Get recent blog posts (for home page hero)
   */
  async getRecentBlogPosts(): Promise<BlogPostSummaryDto[]> {
    const response = await this.client.get('/blog-posts/recent');
    return response.data;
  }

  /**
   * Get detailed blog post by slug
   * @param slug Blog post slug
   * @param preview Include preview content (requires preview token)
   * @param userId Optional user ID from cookie
   */
  async getBlogPost(
    slug: string,
    preview: boolean = false,
    userId?: string
  ): Promise<BlogPostDetailDto> {
    const params: Record<string, any> = { preview };
    const headers: Record<string, string> = {};

    if (userId) {
      headers['Cookie'] = `userId=${userId}`;
    }

    const response = await this.client.get(`/blog-posts/${slug}`, {
      params,
      headers,
    });
    return response.data;
  }

  /**
   * Get all comments for a blog post
   * @param slug Blog post slug
   */
  async getComments(slug: string): Promise<BlogPostCommentsResponseDto> {
    const response = await this.client.get(`/blog-posts/${slug}/comments`);
    return response.data;
  }

  /**
   * Add a comment to a blog post
   * @param slug Blog post slug
   * @param comment Comment data
   * @param userId Optional user ID from cookie
   */
  async addComment(
    slug: string,
    comment: AddCommentRequest,
    userId?: string
  ): Promise<void> {
    const headers: Record<string, string> = {};

    if (userId) {
      headers['Cookie'] = `userId=${userId}`;
    }

    await this.client.post(`/blog-posts/${slug}/comments`, comment, {
      headers,
    });
  }

  /**
   * Get RSS feed
   */
  async getRssFeed(): Promise<string> {
    const response = await this.client.get('/blog-posts/feed.xml', {
      responseType: 'text',
    });
    return response.data;
  }

  // Profile endpoint

  /**
   * Get user profile information
   */
  async getProfile(): Promise<ProfileDto> {
    const response = await this.client.get('/profile');
    return response.data;
  }
}

// Export singleton instance
export const blogApi = new BlogApiClient();

export default blogApi;
