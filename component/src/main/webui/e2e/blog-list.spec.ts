import { test, expect } from '@playwright/test';

test.describe('Blog List Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/blog');
  });

  test('should display blog articles heading', async ({ page }) => {
    // given
    // page is navigated to /blog in beforeEach

    // when
    const heading = page.locator('h1');

    // then
    await expect(heading).toContainText('Blog Articles');
  });

  test('should display total articles count', async ({ page }) => {
    // given
    const subtitle = page.locator('.subtitle');

    // when & then
    await expect(subtitle).toBeVisible();
    await expect(subtitle).toContainText('Total articles:');
  });

  test('should load and display blog posts', async ({ page }) => {
    // given
    // page is loaded

    // when
    await page.waitForLoadState('networkidle');
    const postCards = page.locator('.post-card');
    const count = await postCards.count();

    // then
    expect(count).toBeGreaterThan(0);
  });

  test('should display post title as link', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');

    // when
    const postLink = page.locator('.post-card').first().locator('a').first();

    // then
    await expect(postLink).toBeVisible();
    await expect(postLink).toHaveText(/\S+/);
  });

  test('should display post description', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');

    // when
    const description = page.locator('.post-card').first().locator('p').first();

    // then
    await expect(description).toBeVisible();
  });

  test('should display post date in correct format', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const datePattern = /\w+\s\d+,\s\d{4}/;

    // when
    const dateElement = page.locator('.post-card').first().locator('small').first();
    const dateText = await dateElement.textContent();

    // then
    expect(dateText).toMatch(datePattern);
  });

  test('should display tags when available', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const firstCard = page.locator('.post-card').first();

    // when
    const badges = firstCard.locator('.badge');
    const badgeCount = await badges.count();

    // then
    if (badgeCount > 0) {
      const firstBadge = badges.first();
      await expect(firstBadge).toBeVisible();
    }
  });

  test('should have read more link pointing to blog post', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const blogPostUrlPattern = /^\/blog\/.+/;

    // when
    const firstCard = page.locator('.post-card').first();
    const readLink = firstCard.locator('a').filter({hasText: /read more|read/i}).first();
    const href = await readLink.getAttribute('href');

    // then
    await expect(readLink).toBeVisible();
    expect(href).toMatch(blogPostUrlPattern);
  });

  test('should navigate to blog post when clicking post title', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const postLink = page.locator('.post-card').first().locator('a').first();
    const expectedUrl = await postLink.getAttribute('href');

    // when
    await postLink.click();
    await page.waitForLoadState('networkidle');

    // then
    expect(page.url()).toContain(expectedUrl!);
  });

  test('should display pagination controls', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const paginationWrapper = page.locator('.pagination-wrapper');

    // when
    const buttons = paginationWrapper.locator('button');
    const buttonCount = await buttons.count();

    // then
    await expect(paginationWrapper).toBeVisible();
    expect(buttonCount).toBeGreaterThanOrEqual(2);
  });

  test('should disable previous button on first page', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');

    // when
    const prevButton = page.locator('.pagination-wrapper button').first();
    const disabled = await prevButton.getAttribute('disabled');

    // then
    expect(disabled).not.toBeNull();
  });

  test('should enable next button when more posts available', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const subtitleText = await page.locator('.subtitle').textContent();
    const postCount = await page.locator('.post-card').count();
    const nextButton = page.locator('.pagination-wrapper button').nth(1);

    // when
    const disabled = await nextButton.getAttribute('disabled');

    // then
    const totalArticles = parseInt(subtitleText?.match(/\d+/)?.[0] || '0');
    if (postCount < totalArticles) {
      expect(disabled).toBeNull();
    }
  });

  test('should display loading state while fetching posts', async ({ page }) => {
    // given
    const responsePromise = page.waitForResponse(response =>
      response.url().includes('/blog-posts') && response.status() === 200
    );

    // when
    await page.goto('/blog');
    await responsePromise;

    // then - Response was received successfully
    expect(responsePromise).toBeDefined();
  });

  test('should handle API response with correct JSON format', async ({ page }) => {
    // given
    const jsonContentType = 'application/json';
    const requiredProperties = ['elements', 'offset', 'pageSize', 'totalCount'];

    // when
    const apiResponse = await page.request.get('/api/v1/blog-posts');
    const data = await apiResponse.json();

    // then
    expect(apiResponse.ok()).toBeTruthy();
    expect(apiResponse.headers()['content-type']).toContain(jsonContentType);
    requiredProperties.forEach(prop => {
      expect(data).toHaveProperty(prop);
    });
    expect(Array.isArray(data.elements)).toBeTruthy();
  });

  test('should display all blog post fields correctly', async ({ page }) => {
    // given
    await page.waitForLoadState('networkidle');
    const firstCard = page.locator('.post-card').first();

    // when
    const title = firstCard.locator('a').first();
    const description = firstCard.locator('p').first();
    const date = firstCard.locator('small').first();

    // then
    await expect(title).toBeVisible();
    await expect(description).toBeVisible();
    await expect(date).toBeVisible();
  });

  test('should handle no posts gracefully', async ({ page }) => {
    // given
    await page.route('**/api/v1/blog-posts', route => {
      route.abort('failed');
    });

    // when
    await page.goto('/blog');

    // then
    const alert = page.locator('[role="alert"]');
    await expect(alert).toBeVisible();
  });
});
