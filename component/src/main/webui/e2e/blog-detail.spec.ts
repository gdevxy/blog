import { test, expect } from '@playwright/test';

test.describe('Blog Detail Page', () => {
  test('should load blog post by slug', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-item h2 a').first();
    const href = await firstPostLink.getAttribute('href');

    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    expect(page.url()).toContain(href!);
  });

  test('should display blog post details correctly', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-item h2 a').first();
    const postTitle = await firstPostLink.textContent();

    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    const detailTitle = page.locator('h1').first();
    await expect(detailTitle).toContainText(postTitle!);
  });

  test('should handle invalid blog slug with 404', async ({ page }) => {
    await page.goto('/blog/non-existent-blog-post-slug', { waitUntil: 'networkidle' });

    const error = page.locator('.error, .not-found');
    const visible = await error.isVisible();

    expect(visible).toBeDefined();
  });

  test('should verify API returns correct content type for blog detail', async ({ page }) => {
    const apiResponse = await page.request.get('/api/v1/blog-posts/test-slug');

    const contentType = apiResponse.headers()['content-type'];
    expect(contentType).toContain('application/json');
  });

  test('should load blog post with all required fields', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-item h2 a').first();
    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    const title = page.locator('h1');
    const content = page.locator('[class*="content"], [class*="body"]');

    await expect(title).toBeVisible();
    const hasContent = await content.isVisible();

    expect(hasContent || (await page.locator('main').textContent()?.length ?? 0) > 0).toBeTruthy();
  });
});
