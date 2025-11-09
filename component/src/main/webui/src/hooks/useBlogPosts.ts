import { useState, useEffect, useCallback } from 'react';
import { blogApi } from '@services/api';
import { BlogPost, Page } from '@types/api';

interface UseBlogPostsResult {
  posts: BlogPost[];
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
  const [data, setData] = useState<Page<BlogPost> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [selectedTags, setSelectedTags] = useState<Set<string>>(new Set());

  const fetchPosts = useCallback(
    async (page: number, tags?: Set<string>) => {
      try {
        setLoading(true);
        setError(null);
        const result = await blogApi.getBlogPosts(undefined, tags, pageSize);
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
    fetchPosts(currentPage, selectedTags);
  }, [currentPage, selectedTags, fetchPosts]);

  const goToPage = useCallback(
    async (page: number) => {
      setCurrentPage(page);
    },
    []
  );

  const nextPage = useCallback(async () => {
    if (data && data.offset + data.pageSize < data.totalCount) {
      setCurrentPage((prev) => prev + 1);
    }
  }, [data]);

  const previousPage = useCallback(async () => {
    if (currentPage > 0) {
      setCurrentPage((prev) => prev - 1);
    }
  }, [currentPage]);

  const filterByTag = useCallback(async (tag: string) => {
    if (tag) {
      setSelectedTags(new Set([tag]));
    } else {
      setSelectedTags(new Set());
    }
    setCurrentPage(0);
  }, []);

  const hasNextPage = data ? data.offset + data.pageSize < data.totalCount : false;

  return {
    posts: data?.elements || [],
    loading,
    error,
    currentPage,
    pageSize: data?.pageSize || pageSize,
    hasNextPage,
    totalCount: data?.totalCount || 0,
    goToPage,
    nextPage,
    previousPage,
    filterByTag,
  };
}
