import React from 'react';
import { Link } from 'react-router-dom';
import { useBlogPosts } from '@hooks';
import './HomePage.css';

function HomePage() {
  const { posts, loading, error } = useBlogPosts(0, 5);

  return (
    <div className="home-page">
      <section className="hero">
        <h1>Welcome to gdevxy Blog</h1>
        <p>Exploring Quarkus, Java, and modern web development</p>
        <Link to="/blog" className="cta-button">
          Read Articles
        </Link>
      </section>

      <section className="recent-posts">
        <h2>Recent Articles</h2>
        {loading && <p>Loading articles...</p>}
        {error && <p className="error">Failed to load articles: {error.message}</p>}
        {posts.length > 0 && (
          <div className="posts-grid">
            {posts.map((post) => (
              <article key={post.slug} className="post-card">
                {post.image && (
                  <img src={post.image.url} alt={post.title} className="post-image" />
                )}
                <h3>
                  <Link to={`/blog/${post.slug}`}>{post.title}</Link>
                </h3>
                {post.description && <p>{post.description}</p>}
                <div className="post-meta">
                  <span className="date">
                    {new Date(post.publishedDate).toLocaleDateString()}
                  </span>
                  {post.tags && post.tags.length > 0 && (
                    <div className="tags">
                      {post.tags.map((tag) => (
                        <span key={tag.id} className="tag">
                          {tag.name}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
                <Link to={`/blog/${post.slug}`} className="read-more">
                  Read More →
                </Link>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default HomePage;
