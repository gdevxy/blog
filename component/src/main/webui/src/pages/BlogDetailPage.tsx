import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useBlogPost } from '@hooks';
import './BlogDetailPage.css';

function BlogDetailPage() {
  const { slug } = useParams<{ slug: string }>();
  const { post, loading, error } = useBlogPost(slug || '', false);

  if (!slug) {
    return <div className="error">Invalid blog post slug</div>;
  }

  if (loading) {
    return <div className="loading">Loading article...</div>;
  }

  if (error) {
    return (
      <div className="error">
        Failed to load article: {error.message}
        <br />
        <Link to="/blog">← Back to articles</Link>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="error">
        Article not found
        <br />
        <Link to="/blog">← Back to articles</Link>
      </div>
    );
  }

  return (
    <article className="blog-detail-page">
      <header className="post-header">
        <Link to="/blog" className="back-link">
          ← Back to articles
        </Link>
        <h1>{post.title}</h1>
        <div className="post-meta">
          <time>{new Date(post.publishedDate).toLocaleDateString()}</time>
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
        {post.image && (
          <img src={post.image.url} alt={post.title} className="featured-image" />
        )}
      </header>

      <div className="post-body">
        {post.description && <p className="description">{post.description}</p>}

        {post.blocks && post.blocks.length > 0 && (
          <div className="content">
            {post.blocks.map((block, index) => (
              <div key={index} className={`content-block ${block.node}`}>
                {block.value && <p>{block.value}</p>}
                {block.blocks && block.blocks.length > 0 && (
                  <ul className="nested-list">
                    {block.blocks.map((nestedBlock, nestedIndex) => (
                      <li key={nestedIndex}>{nestedBlock.value}</li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      {post.relatedBlogPosts && post.relatedBlogPosts.length > 0 && (
        <section className="related-posts">
          <h2>Related Articles</h2>
          <ul className="related-list">
            {post.relatedBlogPosts.map((related) => (
              <li key={related.id}>
                <Link to={`/blog/${related.slug}`}>{related.title}</Link>
              </li>
            ))}
          </ul>
        </section>
      )}

      {post.comments && post.comments.length > 0 && (
        <section className="comments-section">
          <h2>Comments ({post.comments.length})</h2>
          <div className="comments-list">
            {post.comments.map((comment) => (
              <div key={comment.id} className="comment">
                <strong>{comment.author}</strong>
                <time>{new Date(comment.createdAt).toLocaleDateString()}</time>
                <p>{comment.comment}</p>
                {comment.replies && comment.replies.length > 0 && (
                  <ul className="replies">
                    {comment.replies.map((reply) => (
                      <li key={reply.id} className="reply">
                        <strong>{reply.author}</strong>
                        <time>{new Date(reply.createdAt).toLocaleDateString()}</time>
                        <p>{reply.comment}</p>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            ))}
          </div>
        </section>
      )}
    </article>
  );
}

export default BlogDetailPage;
