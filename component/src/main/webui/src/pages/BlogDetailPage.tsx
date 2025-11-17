import React, { useState, useRef } from 'react';
import {useParams, Link} from 'react-router-dom';
import {format} from 'date-fns';
import {useBlogPost} from '@hooks';
import {ContentBlockRenderer} from '@components/ContentBlockRenderer';
import RatingBanner from '@components/RatingBanner';
import ScrollProgressBar from '@components/ScrollProgressBar';
import FloatingTableOfContents from '@components/FloatingTableOfContents';
import CommentForm from '@components/CommentForm';
import EditableReply from '@components/EditableReply';
import NotFoundPage from '@pages/NotFoundPage';
import { blogApi } from '@services/api';
import './BlogDetailPage.css';

declare global {
	interface Window {
		grecaptcha: {
			execute: (siteKey: string, options: { action: string }) => Promise<string>;
		};
	}
}

const RECAPTCHA_SITE_KEY = '6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid';

function BlogDetailPage() {
	const {slug} = useParams<{ slug: string }>();
	const {post, loading, error, refetch} = useBlogPost(slug || '', false);
	const [replyingToCommentId, setReplyingToCommentId] = useState<number | null>(null);
	const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
	const [editedCommentText, setEditedCommentText] = useState<string>('');
	const [editError, setEditError] = useState<string | null>(null);
	const [editLoading, setEditLoading] = useState(false);
	const commentsSectionRef = useRef<HTMLElement>(null);

	if (!slug) {
		return <div className="error">Invalid blog post slug</div>;
	}

	if (loading) {
		return <div className="loading">Loading article...</div>;
	}

	if (error || !post) {
		return <NotFoundPage />;
	}

	const handleCommentAdded = async () => {
		setReplyingToCommentId(null);
		await refetch();

		if (commentsSectionRef.current) {
			commentsSectionRef.current.scrollIntoView({ behavior: 'smooth', block: 'start' });
		}
	};

	const handleEditComment = (commentId: number, currentText: string) => {
		setEditingCommentId(commentId);
		setEditedCommentText(currentText);
		setEditError(null);
	};

	const handleSaveComment = async (commentId: number, author: string) => {
		if (!editedCommentText.trim()) {
			setEditError('Comment cannot be empty');
			return;
		}

		try {
			setEditLoading(true);
			setEditError(null);

			if (!window.grecaptcha) {
				setEditError('reCAPTCHA not loaded');
				return;
			}

			const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action: 'comment_update' });

			if (!token) {
				setEditError('Failed to get reCAPTCHA token');
				return;
			}

			await blogApi.updateComment(commentId, {
				author,
				comment: editedCommentText.trim(),
				captcha: token,
				action: 'comment_update',
			} as any);

			setEditingCommentId(null);
			await refetch();
		} catch (err) {
			const message = err instanceof Error ? err.message : 'Failed to update comment';
			setEditError(message);
			console.error('Error updating comment:', err);
		} finally {
			setEditLoading(false);
		}
	};

	const handleCancelEdit = () => {
		setEditingCommentId(null);
		setEditedCommentText('');
		setEditError(null);
	};

	const handleDeleteComment = async (commentId: number) => {
		if (!window.confirm('Are you sure you want to delete this comment?')) {
			return;
		}

		try {
			setEditLoading(true);
			setEditError(null);

			if (!window.grecaptcha) {
				setEditError('reCAPTCHA not loaded');
				return;
			}

			const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action: 'comment_delete' });

			if (!token) {
				setEditError('Failed to get reCAPTCHA token');
				return;
			}

			await blogApi.deleteComment(commentId, {
				captcha: token,
				action: 'comment_delete',
			});

			await refetch();
		} catch (err) {
			const message = err instanceof Error ? err.message : 'Failed to delete comment';
			setEditError(message);
			console.error('Error deleting comment:', err);
		} finally {
			setEditLoading(false);
		}
	};

	return (
		<>
			<ScrollProgressBar />
			<article className="blog-detail-page">
				<header className="post-header">
					{post.image && (
						<div className="hero-section" style={{ backgroundImage: `url(${post.image.url})` }}>
							<div className="hero-overlay"></div>
							<div className="hero-content">
								<h1>{post.title}</h1>
								{post.description && <p className="hero-description">{post.description}</p>}
								<div className="post-meta">
									<time>{format(new Date(post.publishedDate), 'yyyy-MM-dd \'at\' h:mm a')}</time>
									{post.tags && post.tags.filter((tag) => tag.value).length > 0 && (
										<div className="tags">
											{post.tags
												.filter((tag) => tag.value)
												.map((tag) => (
													<span key={tag.code} className="badge text-bg-primary">{tag.value}</span>
												))}
										</div>
									)}
								</div>
							</div>
						</div>
					)}
					{!post.image && (
						<div className="post-header-fallback">
							<h1>{post.title}</h1>
							{post.description && <p className="hero-description">{post.description}</p>}
							<div className="post-meta">
								<time>{format(new Date(post.publishedDate), 'yyyy-MM-dd \'at\' h:mm a')}</time>
								{post.tags && post.tags.filter((tag) => tag.value).length > 0 && (
									<div className="tags">
										{post.tags
											.filter((tag) => tag.value)
											.map((tag) => (
												<span key={tag.code} className="badge text-bg-primary">{tag.value}</span>
											))}
									</div>
								)}
							</div>
						</div>
					)}
			</header>

			<div className="post-body">
				<FloatingTableOfContents />
				{post.blocks && post.blocks.length > 0 && (
					<div className="content">
						{post.blocks.map((block, index) => (
							<ContentBlockRenderer key={index} block={block}/>
						))}
					</div>
				)}
			</div>

			<RatingBanner postId={post.id} liked={post.liked}/>

			<section className="comments-section" ref={commentsSectionRef}>
				<h2>Comments ({post.comments.length})</h2>
				<div className="comments-list">
					{post.comments.map((comment) => (
						<div key={comment.id} className="comment">
							<div className="comment-header">
								<strong>{comment.author || 'Anonymous'}</strong>
								<div className="comment-header-actions">
									<time>{format(new Date(comment.createdAt), 'yyyy-MM-dd \'at\' h:mm a')}</time>
									<button
										className="comment-reply-btn"
										onClick={() => setReplyingToCommentId(replyingToCommentId === comment.id ? null : comment.id)}
										title={replyingToCommentId === comment.id ? 'Cancel reply' : 'Reply to comment'}
									>
										<i className="bi bi-reply-fill"></i>
									</button>
									{comment.modifiable && (
										<>
											<button
												className="comment-edit-btn"
												onClick={() => handleEditComment(comment.id, comment.comment)}
												title="Edit comment"
												disabled={editLoading}
											>
												<i className="bi bi-pencil-square"></i>
											</button>
											<button
												className="comment-delete-btn"
												onClick={() => handleDeleteComment(comment.id)}
												title="Delete comment"
												disabled={editLoading}
											>
												<i className="bi bi-trash3-fill"></i>
											</button>
										</>
									)}
								</div>
							</div>
							{editingCommentId === comment.id ? (
								<div>
									<textarea
										className="edit-textarea-comment"
										value={editedCommentText}
										onChange={(e) => setEditedCommentText(e.target.value)}
										disabled={editLoading}
										rows={4}
									/>
									{editError && <div className="alert alert-danger alert-sm">{editError}</div>}
									<div className="edit-actions-comment">
										<button
											className="btn btn-sm btn-primary"
											onClick={() => handleSaveComment(comment.id, comment.author)}
											disabled={editLoading}
										>
											{editLoading ? 'Saving...' : 'Save'}
										</button>
										<button
											className="btn btn-sm btn-secondary"
											onClick={handleCancelEdit}
											disabled={editLoading}
										>
											Cancel
										</button>
									</div>
								</div>
							) : (
								<p>{comment.comment}</p>
							)}
							{replyingToCommentId === comment.id && (
								<CommentForm
									postId={post.id}
									commentId={comment.id}
									isReply={true}
									onCommentAdded={handleCommentAdded}
									onCancel={() => setReplyingToCommentId(null)}
								/>
							)}
							{comment.replies && comment.replies.length > 0 && (
								<ul className="replies">
									{comment.replies.map((reply) => (
										<EditableReply
											key={reply.id}
											reply={reply}
											onReplyUpdated={refetch}
										/>
									))}
								</ul>
							)}
						</div>
					))}
				</div>

				<CommentForm
					postId={post.id}
					onCommentAdded={handleCommentAdded}
				/>
			</section>
			</article>
			{post.relatedBlogPosts && post.relatedBlogPosts.length > 0 && (
				<section className="related-posts">
					<span class="fst-italic">Related Articles</span>
					<hr />
					<div className="related-list">
						{post.relatedBlogPosts.map((related, index) => (
							<span key={related.id}>
								{index > 0 && <span className="related-separator"> / </span>}
								<Link to={`/blog/${related.slug}`}>{related.title}</Link>
							</span>
						))}
					</div>
				</section>
			)}
		</>
	);
}

export default BlogDetailPage;
