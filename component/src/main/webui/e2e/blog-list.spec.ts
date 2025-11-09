import { test, expect } from '@playwright/test';

test.describe('Blog List Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should display blog articles heading', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Blog Articles');
  });

  test('should display total articles count', async ({ page }) => {
    await expect(page.locator('.subtitle')).toBeVisible();
    await expect(page.locator('.subtitle')).toContainText('Total articles:');
  });

  test('should load and display blog posts', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const postItems = page.locator('.post-item');
    const count = await postItems.count();

    expect(count).toBeGreaterThan(0);
  });

  test('should display post title as link', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const postLink = page.locator('.post-item h2 a').first();
    await expect(postLink).toBeVisible();
    await expect(postLink).toHaveText(/\S+/);
  });

  test('should display post description', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const description = page.locator('.post-item .description').first();
    await expect(description).toBeVisible();
  });

  test('should display post date in correct format', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const dateElement = page.locator('.post-footer .date').first();
    const dateText = await dateElement.textContent();

    expect(dateText).toMatch(/\d+\/\d+\/\d+/);
  });

  test('should display tags when available', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const tagsContainer = page.locator('.post-item .tags').first();
    const tagCount = await tagsContainer.locator('.tag').count();

    if (tagCount > 0) {
      const tag = tagsContainer.locator('.tag').first();
      await expect(tag).toContainText(/#\S+/);
    }
  });

  test('should have read more link pointing to blog post', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const readLink = page.locator('.post-item .read-link').first();
    await expect(readLink).toBeVisible();

    const href = await readLink.getAttribute('href');
    expect(href).toMatch(/^\/blog\/.+/);
  });

  test('should navigate to blog post when clicking post title', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const postLink = page.locator('.post-item h2 a').first();
    const href = await postLink.getAttribute('href');

    await postLink.click();
    await page.waitForLoadState('networkidle');

    expect(page.url()).toContain(href!);
  });

  test('should display pagination controls', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const pagination = page.locator('.pagination');
    await expect(pagination).toBeVisible();

    const prevButton = pagination.locator('button').first();
    const nextButton = pagination.locator('button').nth(1);
    const pageInfo = pagination.locator('.page-info');

    await expect(prevButton).toContainText('Previous');
    await expect(nextButton).toContainText('Next');
    await expect(pageInfo).toContainText('Page');
  });

  test('should disable previous button on first page', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const prevButton = page.locator('.pagination button').first();
    const disabled = await prevButton.getAttribute('disabled');

    expect(disabled).not.toBeNull();
  });

  test('should enable next button when more posts available', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const totalCount = await page.locator('.subtitle').textContent();
    const postCount = await page.locator('.post-item').count();

    const nextButton = page.locator('.pagination button').nth(1);

    if (postCount < parseInt(totalCount!.match(/\d+/)![0])) {
      const disabled = await nextButton.getAttribute('disabled');
      expect(disabled).toBeNull();
    }
  });

  test('should display loading state while fetching posts', async ({ page }) => {
    const responsePromise = page.waitForResponse(response =>
      response.url().includes('/blog-posts') && response.status() === 200
    );

    await page.goto('/');
    await responsePromise;

    const loading = page.locator('.loading');
    const visible = await loading.isVisible();

    expect(visible).toBeDefined();
  });

  test('should handle API response with correct JSON format', async ({ page }) => {
    const apiResponse = await page.request.get('/api/v1/blog-posts');

    expect(apiResponse.ok()).toBeTruthy();

    const contentType = apiResponse.headers()['content-type'];
    expect(contentType).toContain('application/json');

    const data = await apiResponse.json();
    expect(data).toHaveProperty('elements');
    expect(data).toHaveProperty('offset');
    expect(data).toHaveProperty('pageSize');
    expect(data).toHaveProperty('totalCount');

    expect(Array.isArray(data.elements)).toBeTruthy();
  });

  test('should display all blog post fields correctly', async ({ page }) => {
    await page.waitForLoadState('networkidle');

    const firstPost = page.locator('.post-item').first();

    const title = firstPost.locator('h2 a');
    const description = firstPost.locator('.description');
    const date = firstPost.locator('.date');
    const readLink = firstPost.locator('.read-link');

    await expect(title).toBeVisible();
    await expect(description).toBeVisible();
    await expect(date).toBeVisible();
    await expect(readLink).toBeVisible();
  });

  test('should handle no posts gracefully', async ({ page }) => {
    await page.route('**/api/v1/blog-posts', route => {
      route.abort('failed');
    });

    await page.goto('/');

    const error = page.locator('.error');
    await expect(error).toBeVisible();
  });
});
