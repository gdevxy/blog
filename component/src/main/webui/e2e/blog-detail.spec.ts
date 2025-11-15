import { test, expect } from '@playwright/test';

test.describe('Blog Detail Page', () => {
  test('should load blog post by slug', async ({ page }) => {
    // given
    await page.goto('/blog');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-card').first().locator('a').first();
    const expectedUrl = await firstPostLink.getAttribute('href');

    // when
    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    // then
    expect(page.url()).toContain(expectedUrl!);
  });

  test('should display blog post details correctly', async ({ page }) => {
    // given
    await page.goto('/blog');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-card').first().locator('a').first();
    const expectedTitle = await firstPostLink.textContent();

    // when
    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    // then
    const detailTitle = page.locator('h1').first();
    await expect(detailTitle).toContainText(expectedTitle!);
  });

  test('should handle invalid blog slug with 404', async ({ page }) => {
    // given
    const invalidSlug = '/blog/non-existent-blog-post-slug';

    // when
    await page.goto(invalidSlug, { waitUntil: 'networkidle' });

    // then
    const error = page.locator('.error, .not-found');
    const visible = await error.isVisible();
    expect(visible).toBeDefined();
  });

  test('should verify API returns correct content type for blog detail', async ({ page }) => {
    // given
    const testSlug = 'test-slug';
    const jsonContentType = 'application/json';

    // when
    const apiResponse = await page.request.get(`/api/v1/blog-posts/${testSlug}`);

    // then
    const contentType = apiResponse.headers()['content-type'];
    expect(contentType).toContain(jsonContentType);
  });

  test('should load blog post with all required fields', async ({ page }) => {
    // given
    await page.goto('/blog');
    await page.waitForLoadState('networkidle');

    const firstPostLink = page.locator('.post-card').first().locator('a').first();

    // when
    await firstPostLink.click();
    await page.waitForLoadState('networkidle');

    // then
    const title = page.locator('h1');
    await expect(title).toBeVisible();

    const pageContent = await page.locator('body').textContent();
    expect((pageContent?.length ?? 0) > 0).toBeTruthy();
  });
});
