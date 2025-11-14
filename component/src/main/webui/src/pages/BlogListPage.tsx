import React, { useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Card, Alert, Spinner, Row, Col } from 'react-bootstrap';
import { format } from 'date-fns';
import { useBlogPosts } from '@hooks';
import './BlogListPage.css';

function BlogListPage() {
  const { posts, loading, error, currentPage, hasNextPage, totalCount, nextPage, previousPage } =
    useBlogPosts(0, 12);

  useEffect(() => {
    const handleWheel = (e: WheelEvent) => {
      const target = e.target as HTMLElement;
      const tagsContainer = target.closest('.tags-container') as HTMLElement;

      if (!tagsContainer) return;

      e.preventDefault();

      const scrollAmount = e.deltaY > 0 ? 50 : -50;
      tagsContainer.scrollLeft += scrollAmount;
    };

    document.addEventListener('wheel', handleWheel, { passive: false });

    return () => {
      document.removeEventListener('wheel', handleWheel);
    };
  }, []);


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
                <Card className="post-card w-100">
                  {post.image && (
                    <Card.Img variant="top" src={post.image.url} alt={post.title} />
                  )}
                  <Card.Body className="d-flex flex-column">
                    <Card.Title>
                      <Link to={`/blog/${post.slug}`} className="text-decoration-none">
                        {post.title}
                      </Link>
                    </Card.Title>
                    {post.description && (
                      <Card.Text className="text-secondary flex-grow-1">{post.description}</Card.Text>
                    )}
                    <div className="mt-auto">
                      <small className="text-muted d-block mb-2">
                        {format(new Date(post.publishedDate), 'MMM d, yyyy')}
                      </small>
                      {post.tags && post.tags.filter((tag) => tag.value).length > 0 && (
                        <div className="tags-container mb-3">
                          {post.tags
                            .filter((tag) => tag.value)
                            .map((tag) => (
                              <span key={tag.code} className="badge text-bg-primary me-1">
                                {tag.value}
                              </span>
                            ))}
                        </div>
                      )}
                      <Link to={`/blog/${post.slug}`} className="text-primary fw-bold stretched-link">
                        Read More →
                      </Link>
                    </div>
                  </Card.Body>
                </Card>
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
