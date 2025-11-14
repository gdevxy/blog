// API Response Types

export interface BlogPostTag {
  value: string;
  code: string;
}

export interface Image {
  url: string;
  title?: string;
  description?: string;
  width?: number;
  height?: number;
}

export interface BlogPost {
  id?: string;
  slug: string;
  title: string;
  description?: string;
  publishedDate: string;
  image?: Image;
  tags?: BlogPostTag[];
  rating?: string;
}

export interface Page<T> {
  elements: T[];
  offset: number;
  pageSize: number;
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
  marks?: string[];
  blocks?: ContentBlock[];
}

export interface RelatedBlogPost {
  id: string;
  slug: string;
  title: string;
}

export interface BlogPostDetail {
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
  withIndexHeading?: boolean;
}

export interface BlogPostComment {
  id: number;
  author: string;
  comment: string;
  createdAt: string;
  replies: BlogPostComment[];
}

export interface BlogPostCommentAction {
  author: string;
  comment: string;
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
