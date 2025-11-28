import React from 'react';
import {Alert, Spinner, Row, Col} from 'react-bootstrap';
import {useBlogPosts, usePreviewToken} from '@hooks';
import BlogCard from '@components/BlogCard';
import './BlogListPage.css';

function BlogListPage() {
	const previewToken = usePreviewToken();
	const {posts, loading, error, currentPage, hasNextPage, totalCount, nextPage, previousPage} =
		useBlogPosts(0, 12, previewToken);

	return (
		<div className="blog-list-page">
			<div className="mb-4">
				<h1>Blog Articles</h1>
				<p className="subtitle">Total articles: {totalCount}</p>
			</div>

			{loading && (
				<div className="text-center py-5">
					<Spinner animation="border" role="status">
						<span className="visually-hidden">Loading...</span>
					</Spinner>
					<p className="mt-2">Loading articles...</p>
				</div>
			)}

			{error && <Alert variant="danger">Failed to load articles: {error.message}</Alert>}

			{posts.length > 0 && (
				<>
					<Row className="posts-grid g-4">
						{posts.map((post) => (
							<Col key={post.slug} xs={12} sm={6} md={4} className="d-flex">
								<BlogCard post={post} />
							</Col>
						))}
					</Row>

					<div className="pagination-wrapper mt-5 d-flex justify-content-center gap-2">
						<button
							className="pagination-btn"
							onClick={() => previousPage()}
							disabled={currentPage === 0}
							aria-label="Previous page"
						>
							← Previous
						</button>
						<span className="page-info">
              Page {currentPage + 1}
            </span>
						<button
							className="pagination-btn"
							onClick={() => nextPage()}
							disabled={!hasNextPage}
							aria-label="Next page"
						>
							Next →
						</button>
					</div>
				</>
			)}

			{!loading && posts.length === 0 && (
				<Alert variant="info">No articles found.</Alert>
			)}
		</div>
	);
}

export default BlogListPage;
