// API Response Types

export interface BlogPostTag {
  id: string;
  name: string;
}

export interface Image {
  url: string;
  title?: string;
  description?: string;
  width?: number;
  height?: number;
}

export interface BlogPostSummaryDto {
  id?: string;
  slug: string;
  title: string;
  description?: string;
  publishedDate: string;
  image?: Image;
  tags?: BlogPostTag[];
}

export interface BlogPostsResponseDto {
  posts: BlogPostSummaryDto[];
  currentPage: number;
  pageSize: number;
  hasNextPage: boolean;
  totalCount: number;
}

export interface BlogPostSeo {
  title: string;
  description: string;
  robotHint?: string;
}

export interface ContentBlock {
  node: string;
  value?: string;
  marks?: TextMark;
  blocks?: ContentBlock[];
}

export interface TextMark {
  marks: string[];
}

export interface RelatedBlogPost {
  id: string;
  slug: string;
  title: string;
}

export interface BlogPostDetailDto {
  id: string;
  slug: string;
  title: string;
  description: string;
  publishedDate: string;
  image?: Image;
  seo?: BlogPostSeo;
  rating?: string;
  blocks: ContentBlock[];
  tags: BlogPostTag[];
  relatedBlogPosts: RelatedBlogPost[];
  liked?: boolean;
  comments: BlogPostComment[];
  withIndexHeading: boolean;
}

export interface BlogPostComment {
  id: number;
  author: string;
  comment: string;
  createdAt: string;
  replies: BlogPostComment[];
}

export interface BlogPostCommentsResponseDto {
  comments: BlogPostComment[];
  totalCount: number;
}

export interface BlogPostCommentDto {
  id?: number;
  author: string;
  comment: string;
  createdAt?: string;
  replies?: BlogPostCommentDto[];
}

// Profile/About types

export interface AccountDto {
  icon: string;
  url: string;
  type: string;
}

export interface ProfileDto {
  displayName: string;
  email?: string;
  profileUrl?: string;
  avatarUrl?: string;
  location?: string;
  description: string;
  jobTitle?: string;
  company?: string;
  accounts: AccountDto[];
}

// Request types

export interface AddCommentRequest {
  author: string;
  comment: string;
}

// Error types

export interface ErrorResponse {
  statusCode: number;
  message: string;
  errorId: string;
  timestamp: number;
}
