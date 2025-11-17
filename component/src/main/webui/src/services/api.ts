import axios, { AxiosInstance } from 'axios';
import {
  BlogPost,
  BlogPostDetail,
  BlogPostComment,
  BlogPostCommentAction,
  Page,
  ProfileDto,
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
   * @param previewToken Optional preview token
   * @param tags Optional tags to filter
   * @param pageSize Page size for pagination (default: 10)
   */
  async getBlogPosts(
    previewToken?: string,
    tags?: Set<string>,
    pageSize: number = 10
  ): Promise<Page<BlogPost>> {
    const params: Record<string, any> = {};
    if (previewToken) {
      params.previewToken = previewToken;
    }
    if (tags && tags.size > 0) {
      params.tags = Array.from(tags);
    }
    const headers: Record<string, any> = {
      'X-Page-Size': pageSize.toString(),
    };
    const response = await this.client.get('/blog-posts', { params, headers });
    return response.data;
  }


  /**
   * Get detailed blog post by slug
   * @param slug Blog post slug
   * @param previewToken Optional preview token
   */
  async getBlogPost(
    slug: string,
    previewToken?: string
  ): Promise<BlogPostDetail> {
    const params: Record<string, any> = {};

    if (previewToken) {
      params.previewToken = previewToken;
    }

    const response = await this.client.get(`/blog-posts/${slug}`, { params });
    return response.data;
  }

  /**
   * Get all comments for a blog post
   * @param contentfulId Contentful blog post ID
   */
  async getComments(contentfulId: string): Promise<BlogPostComment[]> {
    const response = await this.client.get(`/blog-posts/${contentfulId}/comments`);
    return response.data;
  }

  /**
   * Add a comment to a blog post
   * @param contentfulId Contentful blog post ID
   * @param commentAction Comment action data
   */
  async addComment(
    contentfulId: string,
    commentAction: BlogPostCommentAction
  ): Promise<void> {
    await this.client.post(`/blog-posts/${contentfulId}/comments`, commentAction);
  }

  /**
   * Add a reply to a comment
   * @param contentfulId Contentful blog post ID
   * @param commentId Comment ID to reply to
   * @param replyAction Reply action data
   */
  async addCommentReply(
    contentfulId: string,
    commentId: number,
    replyAction: BlogPostCommentAction
  ): Promise<void> {
    await this.client.post(`/blog-posts/${contentfulId}/comments/${commentId}/reply`, replyAction);
  }

  /**
   * Update an existing comment
   * @param commentId Comment ID to update
   * @param commentAction Updated comment data
   */
  async updateComment(
    commentId: number,
    commentAction: BlogPostCommentAction
  ): Promise<void> {
    await this.client.put(`/comments/${commentId}`, commentAction);
  }

  /**
   * Update an existing comment reply
   * @param replyId Reply ID to update
   * @param commentAction Updated reply data
   */
  async updateCommentReply(
    replyId: number,
    commentAction: BlogPostCommentAction
  ): Promise<void> {
    await this.client.put(`/comments/reply/${replyId}`, commentAction);
  }

  /**
   * Delete an existing comment
   * @param commentId Comment ID to delete
   * @param deleteAction Delete action data with captcha verification
   */
  async deleteComment(
    commentId: number,
    deleteAction: any
  ): Promise<void> {
    await this.client.delete(`/comments/${commentId}`, { data: deleteAction });
  }

  /**
   * Delete an existing comment reply
   * @param replyId Reply ID to delete
   * @param deleteAction Delete action data with captcha verification
   */
  async deleteCommentReply(
    replyId: number,
    deleteAction: any
  ): Promise<void> {
    await this.client.delete(`/comments/reply/${replyId}`, { data: deleteAction });
  }

  /**
   * Mark a blog post as liked with thumbs up
   * @param contentfulId Contentful blog post ID
   * @param likeAction Like action data with captcha verification
   */
  async thumbsUp(contentfulId: string, likeAction: any): Promise<void> {
    await this.client.post(`/blog-posts/${contentfulId}/rating/thumbs-up`, likeAction);
  }

  /**
   * Mark a blog post as disliked with thumbs down
   * @param contentfulId Contentful blog post ID
   */
  async thumbsDown(contentfulId: string): Promise<void> {
    await this.client.post(`/blog-posts/${contentfulId}/rating/thumbs-down`, {});
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
