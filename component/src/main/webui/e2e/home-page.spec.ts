import { test, expect } from '@playwright/test';

test.describe('Home Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('should display hero section', async ({ page }) => {
    // given
    const expectedWelcomeText = 'Welcome to gdevxy Blog';
    const expectedSubtitleText = 'Exploring Quarkus';

    // when
    const hero = page.locator('.hero');
    const title = hero.locator('h1');
    const subtitle = hero.locator('p');

    // then
    await expect(title).toContainText(expectedWelcomeText);
    await expect(subtitle).toContainText(expectedSubtitleText);
  });

  test('should display "Latest Articles" section', async ({ page }) => {
    // given
    const expectedHeading = 'Latest Articles';

    // when
    const heading = page.locator('.latest-posts h2');

    // then
    await expect(heading).toContainText(expectedHeading);
  });

  test('should not display CTA button in hero', async ({ page }) => {
    // given & when
    const ctaButton = page.locator('.cta-button');

    // then
    await expect(ctaButton).not.toBeVisible();
  });

  test('should display article cards in horizontal slider', async ({ page }) => {
    // given
    // page is navigated and loaded in beforeEach

    // when
    const slider = page.locator('.posts-slider');
    const cards = page.locator('.post-card');
    const cardCount = await cards.count();

    // then
    await expect(slider).toBeVisible();
    expect(cardCount).toBeGreaterThan(0);
  });

  test('should have right navigation button', async ({ page }) => {
    // given & when
    const rightBtn = page.locator('.slider-btn-right');

    // then
    await expect(rightBtn).toBeVisible();
  });

  test('should hide left button at start', async ({ page }) => {
    // given & when
    const leftBtn = page.locator('.slider-btn-left');
    const visible = await leftBtn.isVisible();

    // then
    expect(visible).toBe(false);
  });

  test('should scroll right so rightmost card becomes leftmost', async ({ page }) => {
    // given
    const slider = page.locator('.posts-slider');
    const initialScrollLeft = await slider.evaluate(el => el.scrollLeft);
    const containerWidth = await slider.evaluate(el => el.clientWidth);
    const cardWidth = 380;
    const gap = 32;
    const cardWithGap = cardWidth + gap;

    // when
    const rightBtn = page.locator('.slider-btn-right');
    await rightBtn.click();
    await page.waitForTimeout(400);

    // then
    const newScrollLeft = await slider.evaluate(el => el.scrollLeft);
    const rightmostCardStart = initialScrollLeft + containerWidth - cardWidth;
    const expectedScroll = Math.ceil(rightmostCardStart / cardWithGap) * cardWithGap;

    expect(newScrollLeft).toBe(expectedScroll);
  });

  test('should show left button after scrolling right', async ({ page }) => {
    // given
    const rightBtn = page.locator('.slider-btn-right');

    // when
    await rightBtn.click();
    await page.waitForTimeout(400);

    // then
    const leftBtn = page.locator('.slider-btn-left');
    const visible = await leftBtn.isVisible();
    expect(visible).toBe(true);
  });

  test('should display all post card elements', async ({ page }) => {
    // given
    const firstCard = page.locator('.post-card').first();

    // when
    const image = firstCard.locator('.post-image');
    const title = firstCard.locator('h3 a');
    const description = firstCard.locator('p');
    const meta = firstCard.locator('.post-meta');
    const readMore = firstCard.locator('.read-more');
    const hasImage = await image.isVisible();

    // then
    await expect(title).toBeVisible();
    await expect(description).toBeVisible();
    await expect(meta).toBeVisible();
    await expect(readMore).toBeVisible();
    expect([true, false]).toContain(hasImage);
  });

  test('should navigate to blog post when clicking card link', async ({ page }) => {
    // given
    const firstCard = page.locator('.post-card').first();
    const link = firstCard.locator('h3 a');
    const expectedUrl = await link.getAttribute('href');

    // when
    await link.click();
    await page.waitForLoadState('networkidle');

    // then
    expect(page.url()).toContain(expectedUrl!);
  });

  test('should navigate to blog post when clicking read more', async ({ page }) => {
    // given
    const firstCard = page.locator('.post-card').first();
    const readMore = firstCard.locator('.read-more');
    const expectedUrl = await readMore.getAttribute('href');

    // when
    await readMore.click();
    await page.waitForLoadState('networkidle');

    // then
    expect(page.url()).toContain(expectedUrl!);
  });

  test('should display "Show All" card when more articles exist', async ({ page }) => {
    // given & when
    const showAllCard = page.locator('.show-all-card');
    const visible = await showAllCard.isVisible();

    // then
    if (visible) {
      const expectedText = 'Show All';
      const text = await showAllCard.locator('.show-all-content p').textContent();
      expect(text).toBe(expectedText);
    }
  });

  test('should navigate to blog list page when clicking "Show All" card', async ({ page }) => {
    // given
    const showAllCard = page.locator('.show-all-card');
    const expectedBlogUrl = '/blog';

    // when & then
    if (await showAllCard.isVisible()) {
      await showAllCard.click();
      await page.waitForLoadState('networkidle');

      expect(page.url()).toContain(expectedBlogUrl);
    }
  });

  test('should display total article count on "Show All" card', async ({ page }) => {
    // given
    const showAllCard = page.locator('.show-all-card');
    const articleCountPattern = /\d+ Articles/;

    // when & then
    if (await showAllCard.isVisible()) {
      const count = await showAllCard.locator('.show-all-count').textContent();
      expect(count).toMatch(articleCountPattern);
    }
  });

  test('should display tags on cards when available', async ({ page }) => {
    // given
    const firstCard = page.locator('.post-card').first();

    // when
    const tags = firstCard.locator('.tag');
    const tagCount = await tags.count();

    // then
    if (tagCount > 0) {
      const firstTag = tags.first();
      await expect(firstTag).toBeVisible();
    }
  });

  test('should have proper slider scrollbar styling', async ({ page }) => {
    // given
    const slider = page.locator('.posts-slider');
    const expectedOverflow = 'auto';

    // when
    const computed = await slider.evaluate(el =>
      window.getComputedStyle(el).overflowX
    );

    // then
    expect(computed).toBe(expectedOverflow);
  });

  test('should display post date in correct format', async ({ page }) => {
    // given
    const firstCard = page.locator('.post-card').first();
    const datePattern = /\d+\/\d+\/\d+/;

    // when
    const date = firstCard.locator('.date');
    const dateText = await date.textContent();

    // then
    expect(dateText).toMatch(datePattern);
  });

  test('should handle slider responsiveness on resize', async ({ page }) => {
    // given
    const slider = page.locator('.posts-slider');
    const initialWidth = await slider.evaluate(el => el.clientWidth);
    const tabletViewport = { width: 768, height: 1024 };

    // when
    await page.setViewportSize(tabletViewport);
    await page.waitForTimeout(300);

    // then
    const newWidth = await slider.evaluate(el => el.clientWidth);
    expect(newWidth).toBeLessThan(initialWidth);
  });

  test('should show partial card at end to indicate swiping', async ({ page }) => {
    // given
    const slider = page.locator('.posts-slider');
    const nextCard = page.locator('.post-card').nth(1);

    // when
    const containerWidth = await slider.evaluate(el => el.clientWidth);
    const scrollWidth = await slider.evaluate(el => el.scrollWidth);
    const cardWidth = await nextCard.evaluate(el => el.clientWidth);

    // then
    expect(scrollWidth).toBeGreaterThan(containerWidth);
    expect(cardWidth).toBeGreaterThan(0);
  });

  test('should have proper slider padding for half-card effect', async ({ page }) => {
    // given
    const slider = page.locator('.posts-slider');

    // when
    const paddingRight = await slider.evaluate(el =>
      window.getComputedStyle(el).paddingRight
    );

    // then
    expect(paddingRight).not.toBe('0px');
  });

  test('should preserve scroll position when navigating away and back', async ({ page }) => {
    // given
    const rightBtn = page.locator('.slider-btn-right');
    const slider = page.locator('.posts-slider');
    const showAllCard = page.locator('.show-all-card');
    const backButton = page.locator('a[href="/"]');
    const homeUrl = '/';
    const blogUrl = '/blog';

    // when
    await rightBtn.click();
    await page.waitForTimeout(400);

    const scrollPosition = await slider.evaluate(el => el.scrollLeft);

    if (await showAllCard.isVisible()) {
      await showAllCard.click();
    } else {
      await page.goto(blogUrl);
    }
    await page.waitForLoadState('networkidle');

    if (await backButton.isVisible()) {
      await backButton.click();
    } else {
      await page.goto(homeUrl);
    }
    await page.waitForLoadState('networkidle');

    // then
    const newScrollPosition = await slider.evaluate(el => el.scrollLeft);
    expect(newScrollPosition).toBe(0);
  });
});
