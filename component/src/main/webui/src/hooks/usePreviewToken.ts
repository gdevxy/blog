import { useSearchParams } from 'react-router-dom';

export function usePreviewToken(): string | undefined {
  const [searchParams] = useSearchParams();
  return searchParams.get('previewToken') || undefined;
}
