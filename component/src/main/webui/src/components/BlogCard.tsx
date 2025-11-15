import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Card } from 'react-bootstrap';
import { format } from 'date-fns';
import { BlogPost } from '@types/api';
import './BlogCard.css';

interface BlogCardProps {
  post: BlogPost;
}

function BlogCard({ post }: BlogCardProps) {
  const tagsContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleWheel = (e: WheelEvent) => {
      const target = e.target as HTMLElement;
      const tagsContainer = target.closest('.blog-card-tags') as HTMLElement;

      if (!tagsContainer || tagsContainer !== tagsContainerRef.current) return;

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
    <Card className="blog-card h-100">
      {/* Clickable top section: Image, Title, Description */}
      <Link to={`/blog/${post.slug}`} className="blog-card-link">
        {post.image && (
          <Card.Img variant="top" src={post.image.url} alt={post.title} />
        )}
        <Card.Body className="blog-card-content">
          <Card.Title>{post.title}</Card.Title>
          {post.description && (
            <Card.Text className="blog-card-description">{post.description}</Card.Text>
          )}
        </Card.Body>
      </Link>

      {/* Tags section */}
      {post.tags && post.tags.filter((tag) => tag.value).length > 0 && (
        <div className="blog-card-tags-section">
          <div className="blog-card-tags" ref={tagsContainerRef}>
            {post.tags
              .filter((tag) => tag.value)
              .map((tag) => (
                <span key={tag.code} className="badge text-bg-primary">
                  {tag.value}
                </span>
              ))}
          </div>
        </div>
      )}

      {/* Meta section: date and likes */}
      <Card.Footer className="blog-card-meta">
        <div className="blog-card-date">
          {format(new Date(post.publishedDate), 'MMM d, yyyy')}
        </div>
        {post.rating && (
          <div className="blog-card-likes">
            <i className="bi bi-hand-thumbs-up-fill"></i>
            <span>{post.rating}</span>
          </div>
        )}
      </Card.Footer>
    </Card>
  );
}

export default BlogCard;
