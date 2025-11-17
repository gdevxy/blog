import React, { useEffect, useState, useRef } from 'react';
import './FloatingTableOfContents.css';

interface Heading {
	id: string;
	text: string;
	level: number;
}

function FloatingTableOfContents() {
	const [headings, setHeadings] = useState<Heading[]>([]);
	const [activeId, setActiveId] = useState<string>('');
	const tocRef = useRef<HTMLElement>(null);

	useEffect(() => {
		const contentElement = document.querySelector('.blog-detail-page .content');
		if (!contentElement) return;

		const h1Elements = contentElement.querySelectorAll('h1');
		const extractedHeadings: Heading[] = [];

		h1Elements.forEach((heading, index) => {
			if (!heading.id) {
				heading.id = `heading-${index}`;
			}
			extractedHeadings.push({
				id: heading.id,
				text: heading.textContent || '',
				level: 1,
			});
		});

		setHeadings(extractedHeadings);
	}, []);

	useEffect(() => {
		const handleScroll = () => {
			if (!tocRef.current) return;

			const headingElements = headings.map(h => document.getElementById(h.id));
			let currentActive = headings[0]?.id || '';

			for (const heading of headingElements) {
				if (heading && heading.getBoundingClientRect().top < 100) {
					currentActive = heading.id;
				}
			}

			if (currentActive !== activeId) {
				setActiveId(currentActive);
			}

			const postBody = document.querySelector('.post-body') as HTMLElement;

			if (!postBody) return;

			const postBodyRect = postBody.getBoundingClientRect();
			const tocHeight = tocRef.current.offsetHeight;
			const scrollY = window.scrollY;
			const topGap = 48;

			const postBodyTop = postBodyRect.top + scrollY;

			let newTranslateY = 0;

			if (scrollY + topGap > postBodyTop) {
				newTranslateY = scrollY + topGap - postBodyTop;

				const maxTranslate = postBodyRect.height - tocHeight - topGap;
				if (newTranslateY > maxTranslate) {
					newTranslateY = maxTranslate;
				}
			}

			tocRef.current.style.transform = `translateY(${newTranslateY}px)`;
		};

		window.addEventListener('scroll', handleScroll, { passive: true });
		return () => window.removeEventListener('scroll', handleScroll);
	}, [headings, activeId]);

	const handleClick = (id: string) => {
		const element = document.getElementById(id);
		if (element) {
			element.scrollIntoView({ behavior: 'smooth', block: 'start' });
		}
	};

	if (headings.length === 0) return null;

	return (
		<nav className="floating-toc" ref={tocRef}>
			<div className="toc-content">
				<h3 className="toc-title">On this page</h3>
				<ul className="toc-list">
					{headings.map(heading => (
						<li key={heading.id}>
							<button
								className={`toc-link ${activeId === heading.id ? 'active' : ''}`}
								onClick={() => handleClick(heading.id)}
							>
								{heading.text}
							</button>
						</li>
					))}
				</ul>
			</div>
		</nav>
	);
}

export default FloatingTableOfContents;
