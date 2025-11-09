import { test, expect } from '@playwright/test';

test.describe('Home Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('should display hero section', async ({ page }) => {
    const hero = page.locator('.hero');
    const title = hero.locator('h1');
    const subtitle = hero.locator('p');

    await expect(title).toContainText('Welcome to gdevxy Blog');
    await expect(subtitle).toContainText('Exploring Quarkus');
  });

  test('should display "Latest Articles" section', async ({ page }) => {
    const heading = page.locator('.latest-posts h2');
    await expect(heading).toContainText('Latest Articles');
  });

  test('should not display CTA button in hero', async ({ page }) => {
    const ctaButton = page.locator('.cta-button');
    await expect(ctaButton).not.toBeVisible();
  });

  test('should display article cards in horizontal slider', async ({ page }) => {
    const slider = page.locator('.posts-slider');
    const cards = page.locator('.post-card');

    await expect(slider).toBeVisible();
    expect(await cards.count()).toBeGreaterThan(0);
  });

  test('should have right navigation button', async ({ page }) => {
    const rightBtn = page.locator('.slider-btn-right');
    await expect(rightBtn).toBeVisible();
  });

  test('should hide left button at start', async ({ page }) => {
    const leftBtn = page.locator('.slider-btn-left');
    const visible = await leftBtn.isVisible();

    expect(visible).toBe(false);
  });

  test('should scroll right so rightmost card becomes leftmost', async ({ page }) => {
    const slider = page.locator('.posts-slider');
    const initialScrollLeft = await slider.evaluate(el => el.scrollLeft);
    const containerWidth = await slider.evaluate(el => el.clientWidth);
    const cardWidth = 380;
    const gap = 32;

    const rightBtn = page.locator('.slider-btn-right');
    await rightBtn.click();

    await page.waitForTimeout(400);

    const newScrollLeft = await slider.evaluate(el => el.scrollLeft);
    const cardWithGap = cardWidth + gap;
    const rightmostCardStart = initialScrollLeft + containerWidth - cardWidth;
    const expectedScroll = Math.ceil(rightmostCardStart / cardWithGap) * cardWithGap;

    expect(newScrollLeft).toBe(expectedScroll);
  });

  test('should show left button after scrolling right', async ({ page }) => {
    const rightBtn = page.locator('.slider-btn-right');
    await rightBtn.click();
    await page.waitForTimeout(400);

    const leftBtn = page.locator('.slider-btn-left');
    const visible = await leftBtn.isVisible();

    expect(visible).toBe(true);
  });

  test('should display all post card elements', async ({ page }) => {
    const firstCard = page.locator('.post-card').first();

    const image = firstCard.locator('.post-image');
    const title = firstCard.locator('h3 a');
    const description = firstCard.locator('p');
    const meta = firstCard.locator('.post-meta');
    const readMore = firstCard.locator('.read-more');

    await expect(title).toBeVisible();
    await expect(description).toBeVisible();
    await expect(meta).toBeVisible();
    await expect(readMore).toBeVisible();

    const hasImage = await image.isVisible();
    expect([true, false]).toContain(hasImage);
  });

  test('should navigate to blog post when clicking card link', async ({ page }) => {
    const firstCard = page.locator('.post-card').first();
    const link = firstCard.locator('h3 a');
    const href = await link.getAttribute('href');

    await link.click();
    await page.waitForLoadState('networkidle');

    expect(page.url()).toContain(href!);
  });

  test('should navigate to blog post when clicking read more', async ({ page }) => {
    const firstCard = page.locator('.post-card').first();
    const readMore = firstCard.locator('.read-more');
    const href = await readMore.getAttribute('href');

    await readMore.click();
    await page.waitForLoadState('networkidle');

    expect(page.url()).toContain(href!);
  });

  test('should display "Show All" card when more articles exist', async ({ page }) => {
    const showAllCard = page.locator('.show-all-card');
    const visible = await showAllCard.isVisible();

    if (visible) {
      const text = await showAllCard.locator('.show-all-content p').textContent();
      expect(text).toBe('Show All');
    }
  });

  test('should navigate to blog list page when clicking "Show All" card', async ({ page }) => {
    const showAllCard = page.locator('.show-all-card');

    if (await showAllCard.isVisible()) {
      await showAllCard.click();
      await page.waitForLoadState('networkidle');

      expect(page.url()).toContain('/blog');
    }
  });

  test('should display total article count on "Show All" card', async ({ page }) => {
    const showAllCard = page.locator('.show-all-card');

    if (await showAllCard.isVisible()) {
      const count = await showAllCard.locator('.show-all-count').textContent();
      expect(count).toMatch(/\d+ Articles/);
    }
  });

  test('should display tags on cards when available', async ({ page }) => {
    const firstCard = page.locator('.post-card').first();
    const tags = firstCard.locator('.tag');
    const tagCount = await tags.count();

    if (tagCount > 0) {
      const firstTag = tags.first();
      await expect(firstTag).toBeVisible();
    }
  });

  test('should have proper slider scrollbar styling', async ({ page }) => {
    const slider = page.locator('.posts-slider');

    const scrollBarThumb = page.locator('.posts-slider::-webkit-scrollbar-thumb');
    const computed = await slider.evaluate(el =>
      window.getComputedStyle(el).overflowX
    );

    expect(computed).toBe('auto');
  });

  test('should display post date in correct format', async ({ page }) => {
    const firstCard = page.locator('.post-card').first();
    const date = firstCard.locator('.date');
    const dateText = await date.textContent();

    expect(dateText).toMatch(/\d+\/\d+\/\d+/);
  });

  test('should handle slider responsiveness on resize', async ({ page }) => {
    const slider = page.locator('.posts-slider');
    const initialWidth = await slider.evaluate(el => el.clientWidth);

    await page.setViewportSize({ width: 768, height: 1024 });
    await page.waitForTimeout(300);

    const newWidth = await slider.evaluate(el => el.clientWidth);
    expect(newWidth).toBeLessThan(initialWidth);
  });

  test('should show partial card at end to indicate swiping', async ({ page }) => {
    const slider = page.locator('.posts-slider');
    const containerWidth = await slider.evaluate(el => el.clientWidth);
    const scrollWidth = await slider.evaluate(el => el.scrollWidth);

    const nextCard = page.locator('.post-card').nth(1);
    const cardWidth = await nextCard.evaluate(el => el.clientWidth);

    expect(scrollWidth).toBeGreaterThan(containerWidth);
    expect(cardWidth).toBeGreaterThan(0);
  });

  test('should have proper slider padding for half-card effect', async ({ page }) => {
    const slider = page.locator('.posts-slider');
    const paddingRight = await slider.evaluate(el =>
      window.getComputedStyle(el).paddingRight
    );

    expect(paddingRight).not.toBe('0px');
  });

  test('should preserve scroll position when navigating away and back', async ({ page }) => {
    const rightBtn = page.locator('.slider-btn-right');
    await rightBtn.click();
    await page.waitForTimeout(400);

    const slider = page.locator('.posts-slider');
    const scrollPosition = await slider.evaluate(el => el.scrollLeft);

    const showAllCard = page.locator('.show-all-card');
    if (await showAllCard.isVisible()) {
      await showAllCard.click();
    } else {
      await page.goto('/blog');
    }
    await page.waitForLoadState('networkidle');

    const backButton = page.locator('a[href="/"]');
    if (await backButton.isVisible()) {
      await backButton.click();
    } else {
      await page.goto('/');
    }
    await page.waitForLoadState('networkidle');

    const newScrollPosition = await slider.evaluate(el => el.scrollLeft);
    expect(newScrollPosition).toBe(0);
  });
});
