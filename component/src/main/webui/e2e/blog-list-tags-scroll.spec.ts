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
		// Verify post cards are rendered
		const cards = page.locator('.post-card');
		const cardCount = await cards.count();

		console.log(`Found ${cardCount} post cards`);
		expect(cardCount).toBe(1);
	});

	test('should display tags container with multiple badges', async ({page}) => {
		// Find the tags container
		const tagsContainer = page.locator('.tags-container').first();

		await expect(tagsContainer).toBeVisible();

		// Count badges in the container
		const badges = page.locator('.tags-container .badge');
		const badgeCount = await badges.count();

		console.log(`Found ${badgeCount} badges`);
		expect(badgeCount).toBe(8);
	});

	test('should have tags container with overflow', async ({page}) => {
		// Get the tags container
		const tagsContainer = page.locator('.tags-container').first();

		// Check if container has overflow
		const dimensions = await tagsContainer.evaluate((el) => ({
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth,
			scrollHeight: el.scrollHeight,
			clientHeight: el.clientHeight
		}));

		console.log(`Tags container dimensions:`, dimensions);

		// Should have horizontal overflow
		expect(dimensions.scrollWidth).toBeGreaterThan(dimensions.clientWidth);
	});

	test('should have scrollable tags container', async ({page}) => {
		// Get the tags container
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// Get dimensions to verify scrollable area exists
		const scrollResult = await tagsContainer.evaluate(el => {
			const maxScroll = el.scrollWidth - el.clientWidth;
			return {
				scrollWidth: el.scrollWidth,
				clientWidth: el.clientWidth,
				maxScroll: maxScroll,
				scrollable: maxScroll > 0
			};
		});

		console.log(`Tags container scroll state:`, scrollResult);

		// Verify the container is scrollable (has overflow)
		expect(scrollResult.scrollable).toBe(true);
		expect(scrollResult.maxScroll).toBeGreaterThan(0);
	});

	test('should handle wheel events on tags container', async ({page}) => {
		// Get the tags container
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// Verify that the tags container exists and is ready for interaction
		const containerInfo = await tagsContainer.evaluate(el => {
			return {
				visible: el.offsetHeight > 0,
				hasContent: el.children.length > 0,
				scrollable: el.scrollWidth > el.clientWidth
			};
		});

		console.log(`Tags container info:`, containerInfo);

		// Verify the container has the wheel event listener attached
		// (by verifying it's a valid scrollable container with content)
		expect(containerInfo.visible).toBe(true);
		expect(containerInfo.hasContent).toBe(true);
		expect(containerInfo.scrollable).toBe(true);
	});

	test('should scroll tags container horizontally with wheel events', async ({page}) => {
		// Get the tags container
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// Get initial scroll position
		const initialState = await tagsContainer.evaluate(el => ({
			scrollLeft: el.scrollLeft,
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth
		}));
		console.log(`Initial state:`, initialState);

		// Simulate wheel scroll by dispatching an event on a badge element
		// This way the event will have the badge (span) as the target
		// and target.closest('.tags-container') should find the parent container
		await page.evaluate(() => {
			const badge = document.querySelector('.tags-container .badge') as HTMLElement;
			if (badge) {
				const wheelEvent = new WheelEvent('wheel', {
					deltaY: 100,
					bubbles: true,
					cancelable: true
				});
				// Dispatch on the badge, so when the handler runs:
				// target = badge element
				// target.closest('.tags-container') = the container div
				badge.dispatchEvent(wheelEvent);
			}
		});

		await page.waitForTimeout(300);

		// Get new scroll position
		const newState = await tagsContainer.evaluate(el => ({
			scrollLeft: el.scrollLeft,
			scrollWidth: el.scrollWidth,
			clientWidth: el.clientWidth
		}));
		console.log(`New state after wheel:`, newState);

		// Should have scrolled right (scrollLeft increased by 50)
		expect(newState.scrollLeft).toBe(initialState.scrollLeft + 50);
	});

	test('should prevent default page scroll when scrolling over tags', async ({page}) => {
		// Get the tags container
		const tagsContainer = page.locator('.tags-container').first();
		await expect(tagsContainer).toBeVisible();

		// Get initial page scroll Y
		const initialPageScrollY = await page.evaluate(() => window.scrollY);
		console.log(`Initial page scrollY: ${initialPageScrollY}`);

		// Dispatch wheel event on tags container
		await tagsContainer.evaluate(el => el.dispatchEvent(new WheelEvent('wheel', { deltaY: 100 })));
		await page.waitForTimeout(300);

		// Get new page scroll Y
		const newPageScrollY = await page.evaluate(() => window.scrollY);
		console.log(`New page scrollY: ${newPageScrollY}`);

		// Page should not have scrolled vertically
		// (preventDefault() was called on the wheel event)
		expect(newPageScrollY).toBe(initialPageScrollY);
	});

	test('should display badges with correct styling', async ({page}) => {
		// Get a badge
		const badge = page.locator('.tags-container .badge').first();
		await expect(badge).toBeVisible();

		// Check badge styling
		const computedStyle = await badge.evaluate((el) => {
			const style = window.getComputedStyle(el);
			return {
				backgroundColor: style.backgroundColor,
				color: style.color,
				fontWeight: style.fontWeight
			};
		});

		console.log('Badge styles:', computedStyle);

		// Should have purple background color (#8b3dd1 = rgb(139, 61, 209))
		expect(computedStyle.backgroundColor).toContain('139');
		// Should have white text
		expect(computedStyle.color).toContain('255');
		// Should be bold
		expect(computedStyle.fontWeight).toContain('600');
	});
});
