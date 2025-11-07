import React from 'react';
import { Link } from 'react-router-dom';
import { useBlogPosts } from '@hooks';
import './BlogListPage.css';

function BlogListPage() {
  const { posts, loading, error, currentPage, hasNextPage, totalCount, nextPage, previousPage } =
    useBlogPosts(0, 10);

  return (
    <div className="blog-list-page">
      <h1>Blog Articles</h1>
      <p className="subtitle">Total articles: {totalCount}</p>

      {loading && <p className="loading">Loading articles...</p>}
      {error && <p className="error">Failed to load articles: {error.message}</p>}

      {posts.length > 0 && (
        <>
          <div className="posts-list">
            {posts.map((post) => (
              <article key={post.slug} className="post-item">
                <div className="post-content">
                  <h2>
                    <Link to={`/blog/${post.slug}`}>{post.title}</Link>
                  </h2>
                  {post.description && <p className="description">{post.description}</p>}
                  <div className="post-footer">
                    <span className="date">
                      {new Date(post.publishedDate).toLocaleDateString()}
                    </span>
                    {post.tags && post.tags.length > 0 && (
                      <div className="tags">
                        {post.tags.map((tag) => (
                          <span key={tag.id} className="tag">
                            #{tag.name}
                          </span>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
                <Link to={`/blog/${post.slug}`} className="read-link">
                  →
                </Link>
              </article>
            ))}
          </div>

          <div className="pagination">
            <button onClick={previousPage} disabled={currentPage === 0} className="btn-nav">
              ← Previous
            </button>
            <span className="page-info">
              Page {currentPage + 1}
            </span>
            <button onClick={nextPage} disabled={!hasNextPage} className="btn-nav">
              Next →
            </button>
          </div>
        </>
      )}

      {!loading && posts.length === 0 && (
        <p className="no-posts">No articles found.</p>
      )}
    </div>
  );
}

export default BlogListPage;
