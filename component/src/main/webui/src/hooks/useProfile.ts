import { useState, useEffect } from 'react';
import { blogApi } from '@services/api';
import { ProfileDto } from '@types/api';

interface UseProfileResult {
  profile: ProfileDto | null;
  loading: boolean;
  error: Error | null;
}

export function useProfile(previewToken?: string): UseProfileResult {
  const [profile, setProfile] = useState<ProfileDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        setError(null);
        const result = await blogApi.getProfile(previewToken);
        setProfile(result);
      } catch (err) {
        const error = err instanceof Error ? err : new Error(String(err));
        setError(error);
        console.error('Failed to fetch profile:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [previewToken]);

  return { profile, loading, error };
}
