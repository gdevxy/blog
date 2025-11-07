import { useState, useEffect } from 'react';
import { blogApi } from '@services/api';
import { BlogPostDetailDto } from '@types/api';

interface UseBlogPostResult {
  post: BlogPostDetailDto | null;
  loading: boolean;
  error: Error | null;
}

export function useBlogPost(slug: string, preview: boolean = false): UseBlogPostResult {
  const [post, setPost] = useState<BlogPostDetailDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchPost = async () => {
      try {
        setLoading(true);
        setError(null);
        const result = await blogApi.getBlogPost(slug, preview);
        setPost(result);
      } catch (err) {
        const error = err instanceof Error ? err : new Error(String(err));
        setError(error);
        console.error('Failed to fetch blog post:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPost();
  }, [slug, preview]);

  return { post, loading, error };
}
