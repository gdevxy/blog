import React, {useEffect, useState} from 'react';
import {ContentBlock} from '@types/api';
import hljs from 'highlight.js';
import '@/styles/ContentBlockRenderer.css';

// Dynamically import highlight.js themes
const loadHighlightTheme = (isDark: boolean) => {
	// Remove existing highlight.js stylesheet if it exists
	const existingLink = document.head.querySelector('link[data-highlight-theme]');
	if (existingLink) {
		existingLink.remove();
	}

	// Create and add the appropriate theme
	const link = document.createElement('link');
	link.rel = 'stylesheet';
	link.href = isDark
		? 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css'
		: 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-light.min.css';
	link.setAttribute('data-highlight-theme', 'true');
	document.head.appendChild(link);
};

interface ContentBlockRendererProps {
	block: ContentBlock;
}

function useHighlightTheme() {
	useEffect(() => {
		const checkTheme = () => {
			const isDark = !document.documentElement.hasAttribute('data-theme') ||
				document.documentElement.getAttribute('data-theme') !== 'light';
			loadHighlightTheme(isDark);
		};

		// Load theme on mount
		checkTheme();

		// Watch for theme changes
		const observer = new MutationObserver(checkTheme);
		observer.observe(document.documentElement, {attributes: true, attributeFilter: ['data-theme']});

		return () => observer.disconnect();
	}, []);
}

function renderMarkedText(value: string, marks: string[] | undefined): React.ReactNode {
	if (!value) return null;

	const markSet = marks || [];

	let element: React.ReactNode = value;

	if (markSet.includes('code')) {
		element = <code className="inline-code">{element}</code>;
	}

	if (markSet.includes('bold')) {
		element = <strong>{element}</strong>;
	}

	if (markSet.includes('italic')) {
		element = <em>{element}</em>;
	}

	if (markSet.includes('underline')) {
		element = <u>{element}</u>;
	}

	if (markSet.includes('superscript')) {
		element = <sup>{element}</sup>;
	}

	if (markSet.includes('subscript')) {
		element = <sub>{element}</sub>;
	}

	return element;
}

function renderTextNode(block: ContentBlock): React.ReactNode {
	return renderMarkedText(block.value || '', block.marks);
}

function renderParagraph(block: ContentBlock): React.ReactNode {
	if (block.blocks && block.blocks.length > 0) {
		const isCodeBlock =
			block.blocks.length === 1 &&
			block.blocks[0].node === 'text' &&
			block.blocks[0].marks &&
			block.blocks[0].marks.includes('code');

		if (isCodeBlock) {
			return renderCodeBlock(block.blocks[0]);
		}

		return (
			<p className="paragraph">
				{block.blocks.map((child, index) => (
					<React.Fragment key={index}>
						{renderNodeContent(child)}
					</React.Fragment>
				))}
			</p>
		);
	}

	return <p className="paragraph">{renderMarkedText(block.value || '', block.marks)}</p>;
}

function renderHeading(level: number, block: ContentBlock): React.ReactNode {
	const HeadingTag = `h${level}` as const;
	const classNames = ['heading', `heading${level}`].join(' ');

	if (block.blocks && block.blocks.length > 0) {
		return React.createElement(
			HeadingTag,
			{className: classNames},
			block.blocks.map((child, index) => (
				<React.Fragment key={index}>
					{renderNodeContent(child)}
				</React.Fragment>
			))
		);
	}

	return React.createElement(
		HeadingTag,
		{className: classNames},
		renderMarkedText(block.value || '', block.marks)
	);
}

function renderList(isOrdered: boolean, block: ContentBlock): React.ReactNode {
	const ListTag = isOrdered ? 'ol' : 'ul';
	const classNames = isOrdered ? 'ordered-list' : 'unordered-list';

	return React.createElement(
		ListTag,
		{className: classNames},
		(block.blocks || []).map((item, index) => (
			<li key={index} className="list-item">
				{renderNodeContent(item)}
			</li>
		))
	);
}

function renderListItem(block: ContentBlock): React.ReactNode {
	if (block.blocks && block.blocks.length > 0) {
		return (
			<>
				{block.blocks.map((child, index) => (
					<React.Fragment key={index}>
						{renderNodeContent(child)}
					</React.Fragment>
				))}
			</>
		);
	}

	return renderMarkedText(block.value || '', block.marks);
}

function renderQuote(block: ContentBlock): React.ReactNode {
	if (block.blocks && block.blocks.length > 0) {
		return (
			<blockquote className="quote">
				{block.blocks.map((child, index) => (
					<React.Fragment key={index}>
						{renderNodeContent(child)}
						{index < block.blocks!.length - 1 && <br/>}
					</React.Fragment>
				))}
			</blockquote>
		);
	}

	return (
		<blockquote className="quote">
			{renderMarkedText(block.value || '', block.marks)}
		</blockquote>
	);
}

function renderHorizontalRule(): React.ReactNode {
	return <hr className="horizontal-rule"/>;
}

function renderCodeBlock(block: ContentBlock): React.ReactNode {

	let codeContent = '';

	if (block.value) {
		codeContent = block.value;
	} else if (block.blocks && block.blocks.length > 0) {
		codeContent = block.blocks.map((b) => b.value || '').join('\n');
	}

	const regex = /^\/\/\s*language:\s*(\w+)\s*[\r\n]+([\s\S]*)$/m;
	const match = codeContent.match(regex);

	let language = 'auto';
	if (match) {
		language = match[1].trim();
		codeContent = match[2];
	}

	let highlightedCode;
	try {
		if (language && language !== 'auto') {
			highlightedCode = hljs.highlight(codeContent, {language, ignoreIllegals: true}).value;
		} else {
			highlightedCode = hljs.highlightAuto(codeContent).value;
		}
	} catch (e) {
		highlightedCode = hljs.highlightAuto(codeContent).value;
	}

	return (
		<div className="code-block-wrapper">
			{language !== 'plaintext' && <div className="code-language-badge">{language}</div>}
			<pre className="code-block">
				<code className={language && language !== 'plaintext' ? `hljs language-${language}` : 'hljs'} dangerouslySetInnerHTML={{__html: highlightedCode}} />
			</pre>
		</div>
	);
}

function renderTable(block: ContentBlock): React.ReactNode {
	return (
		<div className="table-wrapper">
			<table className="content-table">
				<tbody>
				{(block.blocks || []).map((row, rowIndex) => (
					<tr key={rowIndex}>
						{(row.blocks || []).map((cell, cellIndex) => (
							<td key={cellIndex} className="table-cell">
								{renderNodeContent(cell)}
							</td>
						))}
					</tr>
				))}
				</tbody>
			</table>
		</div>
	);
}

function renderTableRow(block: ContentBlock): React.ReactNode {
	return (
		<>
			{(block.blocks || []).map((cell, index) => (
				<React.Fragment key={index}>{renderNodeContent(cell)}</React.Fragment>
			))}
		</>
	);
}

function renderTableCell(block: ContentBlock): React.ReactNode {
	if (block.blocks && block.blocks.length > 0) {
		return (
			<>
				{block.blocks.map((child, index) => (
					<React.Fragment key={index}>
						{renderNodeContent(child)}
					</React.Fragment>
				))}
			</>
		);
	}

	return renderMarkedText(block.value || '', block.marks);
}

function renderHyperlink(block: ContentBlock): React.ReactNode {
	if (block.blocks && block.blocks.length > 0) {
		return (
			<a href={block.value || '#'} className="hyperlink" target="_blank" rel="noopener noreferrer">
				{block.blocks.map((child, index) => (
					<React.Fragment key={index}>
						{renderNodeContent(child)}
					</React.Fragment>
				))}
			</a>
		);
	}

	return (
		<a href={block.value || '#'} className="hyperlink" target="_blank" rel="noopener noreferrer">
			{block.value || 'Link'}
		</a>
	);
}

function renderEmbeddedEntryBlock(block: ContentBlock): React.ReactNode {
	if (!block.image) {
		return <div className="embedded-placeholder">[Embedded content - Missing image]</div>;
	}

	const altText = block.title || block.description || 'Embedded content';
	const style: React.CSSProperties = {};

	if (block.width !== undefined && block.height !== undefined) {
		style.width = `${block.width}px`;
		style.height = `${block.height}px`;
	} else if (block.width !== undefined) {
		style.width = `${block.width}px`;
	} else if (block.height !== undefined) {
		style.height = `${block.height}px`;
	}

	if (block.fullWidth) {
		return (
			<figure style={{margin: '1.5rem 0', ...style}}>
				<img
					src={block.image.url}
					alt={altText}
					title={block.title}
					style={{width: '100%', height: 'auto', display: 'block'}}
				/>
				{block.description && <figcaption style={{
					marginTop: '0.5rem',
					fontSize: '0.9rem',
					color: 'var(--text-tertiary)'
				}}>{block.description}</figcaption>}
			</figure>
		);
	}

	return (
		<figure style={{margin: '1.5rem 0', ...style}}>
			<img
				src={block.image.url}
				alt={altText}
				title={block.title}
				style={{width: '100%', height: 'auto', display: 'block'}}
			/>
			{block.description && <figcaption style={{
				marginTop: '0.5rem',
				fontSize: '0.9rem',
				color: 'var(--text-tertiary)'
			}}>{block.description}</figcaption>}
		</figure>
	);
}

function renderNodeContent(block: ContentBlock): React.ReactNode {
	switch (block.node) {
		case 'text':
			return renderTextNode(block);

		case 'paragraph':
			return renderParagraph(block);

		case 'heading-1':
			return renderHeading(1, block);
		case 'heading-2':
			return renderHeading(2, block);
		case 'heading-3':
			return renderHeading(3, block);
		case 'heading-4':
			return renderHeading(4, block);
		case 'heading-5':
			return renderHeading(5, block);
		case 'heading-6':
			return renderHeading(6, block);

		case 'ordered-list':
			return renderList(true, block);
		case 'unordered-list':
			return renderList(false, block);
		case 'list-item':
			return renderListItem(block);

		case 'blockquote':
			return renderQuote(block);

		case 'hr':
			return renderHorizontalRule();

		case 'hyperlink':
			return renderHyperlink(block);

		case 'table':
			return renderTable(block);
		case 'table-row':
			return renderTableRow(block);
		case 'table-cell':
		case 'table-header-cell':
			return renderTableCell(block);

		case 'embedded-entry-block':
			return renderEmbeddedEntryBlock(block);

		case 'embedded-asset-block':
		case 'embedded-resource-block':
			return <div className="embedded-placeholder">[Embedded content]</div>;

		case 'document':
		default:
			if (block.blocks && block.blocks.length > 0) {
				return block.blocks.map((child, index) => (
					<React.Fragment key={index}>{renderNodeContent(child)}</React.Fragment>
				));
			}
			return null;
	}
}

export function ContentBlockRenderer({block}: ContentBlockRendererProps) {
	useHighlightTheme();
	return <div className="content-block-renderer">{renderNodeContent(block)}</div>;
}
