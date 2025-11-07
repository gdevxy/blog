import { useState, useEffect, useCallback } from 'react';
import { blogApi } from '@services/api';
import { BlogPostsResponseDto, BlogPostSummaryDto } from '@types/api';

interface UseBlogPostsResult {
  posts: BlogPostSummaryDto[];
  loading: boolean;
  error: Error | null;
  currentPage: number;
  pageSize: number;
  hasNextPage: boolean;
  totalCount: number;
  goToPage: (page: number) => Promise<void>;
  nextPage: () => Promise<void>;
  previousPage: () => Promise<void>;
  filterByTag: (tag: string) => Promise<void>;
}

export function useBlogPosts(initialPage: number = 0, pageSize: number = 10): UseBlogPostsResult {
  const [data, setData] = useState<BlogPostsResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [currentTag, setCurrentTag] = useState<string | undefined>();

  const fetchPosts = useCallback(
    async (page: number, tag?: string) => {
      try {
        setLoading(true);
        setError(null);
        const result = await blogApi.getBlogPosts(page, pageSize, tag);
        setData(result);
        setCurrentPage(page);
      } catch (err) {
        const error = err instanceof Error ? err : new Error(String(err));
        setError(error);
        console.error('Failed to fetch blog posts:', error);
      } finally {
        setLoading(false);
      }
    },
    [pageSize]
  );

  useEffect(() => {
    fetchPosts(currentPage, currentTag);
  }, [currentPage, currentTag, fetchPosts]);

  const goToPage = useCallback(
    async (page: number) => {
      setCurrentPage(page);
    },
    []
  );

  const nextPage = useCallback(async () => {
    if (data?.hasNextPage) {
      setCurrentPage((prev) => prev + 1);
    }
  }, [data?.hasNextPage]);

  const previousPage = useCallback(async () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  }, [currentPage]);

  const filterByTag = useCallback(async (tag: string) => {
    setCurrentTag(tag || undefined);
    setCurrentPage(0);
  }, []);

  return {
    posts: data?.posts || [],
    loading,
    error,
    currentPage: data?.currentPage || 0,
    pageSize: data?.pageSize || pageSize,
    hasNextPage: data?.hasNextPage || false,
    totalCount: data?.totalCount || 0,
    goToPage,
    nextPage,
    previousPage,
    filterByTag,
  };
}
