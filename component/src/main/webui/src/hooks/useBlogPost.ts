import { useState, useEffect, useCallback } from 'react';
import { blogApi } from '@services/api';
import { BlogPostDetailDto } from '@types/api';

interface UseBlogPostResult {
  post: BlogPostDetailDto | null;
  loading: boolean;
  error: Error | null;
  refetch: () => Promise<void>;
}

export function useBlogPost(slug: string, preview: boolean = false): UseBlogPostResult {
  const [post, setPost] = useState<BlogPostDetailDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [isRefetching, setIsRefetching] = useState(false);

  const fetchPost = useCallback(async (isRefetch: boolean = false) => {
    try {
      if (!isRefetch) {
        setLoading(true);
      } else {
        setIsRefetching(true);
      }
      setError(null);
      const result = await blogApi.getBlogPost(slug, preview);
      setPost(result);
    } catch (err) {
      const error = err instanceof Error ? err : new Error(String(err));
      setError(error);
      console.error('Failed to fetch blog post:', error);
    } finally {
      if (!isRefetch) {
        setLoading(false);
      } else {
        setIsRefetching(false);
      }
    }
  }, [slug, preview]);

  useEffect(() => {
    fetchPost(false);
  }, [fetchPost]);

  const refetch = useCallback(async () => {
    await fetchPost(true);
  }, [fetchPost]);

  return { post, loading, error, refetch };
}
