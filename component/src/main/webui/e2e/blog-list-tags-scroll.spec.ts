import {expect, test} from '@playwright/test';

test.describe('Blog List Page - Tags Horizontal Scroll', () => {
	test.beforeEach(async ({page, context}) => {
		// Set up route handler BEFORE navigation to intercept API calls
		await page.route('**/api/v1/blog-posts*', async (route) => {
			const mockData = {
				elements: [
					{
						id: '1',
						slug: 'test-post-1',
						title: 'Test Post with Many Tags',
						description: 'This is a test post with many tags to test horizontal scrolling',
						publishedDate: '2025-01-19T14:41:00Z',
						image: {
							url: 'https://via.placeholder.com/300x200?text=Test+Image',
							title: 'Test Image'
						},
						tags: [
							{value: 'JavaScript', code: 'javascript'},
							{value: 'TypeScript', code: 'typescript'},
							{value: 'React', code: 'react'},
							{value: 'Testing', code: 'testing'},
							{value: 'Playwright', code: 'playwright'},
							{value: 'E2E', code: 'e2e'},
							{value: 'Frontend', code: 'frontend'},
							{value: 'Development', code: 'development'}
						],
						rating: '5'
					}
				],
				offset: 0,
				pageSize: 12,
				totalCount: 1
			};
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify(mockData)
			});
		});

		// Navigate to /blog page (uses baseURL from playwright config - localhost:3000)
		await page.goto('/blog');

		// Wait for the mock data to be loaded and rendered
		await page.waitForSelector('.post-card', { timeout: 10000 });
	});

	test('should display post cards with tags', async ({page}) => {
		// given
		// mock data is set up in beforeEach with 1 post card

		// when
		const cards = page.locator('.post-card');
		const cardCount = await cards.count();

		// then
		expect(cardCount).toBe(1);
	});

	test('should display tags container with multiple badges', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();

		// when
		const badges = page.locator('.tags-container .badge');
		const badgeCount = await badges.count();

		// then
		await expect(tagsContainer).toBeVisible();
		expect(badgeCount).toBe(8);
	});

	test('should have tags container with overflow', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();

		// when
		const dimensions = await tagsContainer.evaluate((el) => ({
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth,
			scrollHeight: el.scrollHeight,
			clientHeight: el.clientHeight
		}));

		// then
		expect(dimensions.scrollWidth).toBeGreaterThan(dimensions.clientWidth);
	});

	test('should have scrollable tags container', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// when
		const scrollResult = await tagsContainer.evaluate(el => {
			const maxScroll = el.scrollWidth - el.clientWidth;
			return {
				scrollWidth: el.scrollWidth,
				clientWidth: el.clientWidth,
				maxScroll: maxScroll,
				scrollable: maxScroll > 0
			};
		});

		// then
		expect(scrollResult.scrollable).toBe(true);
		expect(scrollResult.maxScroll).toBeGreaterThan(0);
	});

	test('should handle wheel events on tags container', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// when
		const containerInfo = await tagsContainer.evaluate(el => {
			return {
				visible: el.offsetHeight > 0,
				hasContent: el.children.length > 0,
				scrollable: el.scrollWidth > el.clientWidth
			};
		});

		// then
		expect(containerInfo.visible).toBe(true);
		expect(containerInfo.hasContent).toBe(true);
		expect(containerInfo.scrollable).toBe(true);
	});

	test('should scroll tags container horizontally with wheel events', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		const initialState = await tagsContainer.evaluate(el => ({
			scrollLeft: el.scrollLeft,
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth
		}));

		// when
		// Use mouse wheel over the tags container to trigger the actual wheel event handler
		const badge = page.locator('.tags-container .badge').first();
		await badge.hover();
		await page.mouse.wheel(0, 100);

		await page.waitForTimeout(300);

		// then
		const newState = await tagsContainer.evaluate(el => ({
			scrollLeft: el.scrollLeft,
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth
		}));

		// Allow for small variance in scroll amount (browsers may handle wheel events slightly differently)
		const scrollDifference = newState.scrollLeft - initialState.scrollLeft;
		expect(scrollDifference).toBeGreaterThan(40);
		expect(scrollDifference).toBeLessThan(60);
	});

	test('should prevent default page scroll when scrolling over tags', async ({page}) => {
		// given
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		const initialPageScrollY = await page.evaluate(() => window.scrollY);

		// when
		await tagsContainer.evaluate(el => el.dispatchEvent(new WheelEvent('wheel', { deltaY: 100 })));
		await page.waitForTimeout(300);

		// then
		const newPageScrollY = await page.evaluate(() => window.scrollY);
		expect(newPageScrollY).toBe(initialPageScrollY);
	});

	test('should display badges with correct styling', async ({page}) => {
		// given
		const purpleRgbComponent = '139';
		const whiteRgbComponent = '255';
		const boldFontWeight = '600';

		// when
		const badge = page.locator('.tags-container .badge').first();
		await expect(badge).toBeVisible();

		const computedStyle = await badge.evaluate((el) => {
			const style = window.getComputedStyle(el);
			return {
				backgroundColor: style.backgroundColor,
				color: style.color,
				fontWeight: style.fontWeight
			};
		});

		// then
		expect(computedStyle.backgroundColor).toContain(purpleRgbComponent);
		expect(computedStyle.color).toContain(whiteRgbComponent);
		expect(computedStyle.fontWeight).toContain(boldFontWeight);
	});
});
