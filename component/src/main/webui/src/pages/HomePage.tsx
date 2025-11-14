import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { Card, Spinner } from 'react-bootstrap';
import { format } from 'date-fns';
import { useBlogPosts } from '@hooks';
import './HomePage.css';

function HomePage() {
  const pageSize = 7;
  const { posts, loading, error, totalCount } = useBlogPosts(0, pageSize);
  const sliderRef = useRef<HTMLDivElement>(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(true);
  const [isAtEnd, setIsAtEnd] = useState(false);

  const checkScroll = () => {
    if (!sliderRef.current) return;
    const { scrollLeft, scrollWidth, clientWidth } = sliderRef.current;
    setCanScrollLeft(scrollLeft > 0);
    const atEnd = scrollLeft >= scrollWidth - clientWidth - 10;
    setCanScrollRight(!atEnd);
    setIsAtEnd(atEnd);
  };

  const scroll = (direction: 'left' | 'right') => {
    if (!sliderRef.current) return;
    const slider = sliderRef.current;
    const cardWidth = 380;
    const gap = 32;
    const cardWithGap = cardWidth + gap;
    const containerWidth = slider.clientWidth;

    if (direction === 'right') {
      const rightmostCardStart = slider.scrollLeft + containerWidth - cardWidth;
      const nextCardStart = Math.ceil(rightmostCardStart / cardWithGap) * cardWithGap;
      slider.scrollTo({
        left: nextCardStart,
        behavior: 'smooth',
      });
    } else {
      const leftmostCardStart = slider.scrollLeft;
      const prevCardStart = Math.max(0, leftmostCardStart - containerWidth);
      const snappedPrevStart = Math.floor(prevCardStart / cardWithGap) * cardWithGap;
      slider.scrollTo({
        left: snappedPrevStart,
        behavior: 'smooth',
      });
    }

    setTimeout(checkScroll, 300);
  };

  React.useEffect(() => {
    checkScroll();
    window.addEventListener('resize', checkScroll);
    return () => window.removeEventListener('resize', checkScroll);
  }, [posts]);

  return (
    <div className="home-page">
      <section className="hero">
        <h1>Welcome to gdevxy Blog</h1>
        <p>Exploring Quarkus, Java, and modern web development</p>
      </section>

      <section className="latest-posts">
        <h2>Latest Articles</h2>
        {loading && (
          <div className="text-center py-5">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
            <p className="mt-2 text-secondary">Loading articles...</p>
          </div>
        )}
        {error && <div className="alert alert-danger">Failed to load articles: {error.message}</div>}
        {posts.length > 0 && (
          <div className="slider-container">
            {canScrollLeft && (
              <button
                className="slider-btn slider-btn-left"
                onClick={() => scroll('left')}
                aria-label="Scroll left"
              >
                ←
              </button>
            )}
            <div className="posts-slider" ref={sliderRef} onScroll={checkScroll}>
              {posts.map((post) => (
                <Link key={post.slug} to={`/blog/${post.slug}`} className="post-card-wrapper">
                  <Card className="post-card h-100">
                    {post.image && (
                      <Card.Img variant="top" src={post.image.url} alt={post.title} />
                    )}
                    <Card.Body className="d-flex flex-column">
                      <Card.Title>{post.title}</Card.Title>
                      {post.description && (
                        <Card.Text className="text-secondary flex-grow-1">{post.description}</Card.Text>
                      )}
                      <div className="post-meta mt-auto">
                        <small className="text-muted">
                          {format(new Date(post.publishedDate), 'MMM d, yyyy')}
                        </small>
                        {post.rating && (
                          <span className="likes">
                            👍 {post.rating}
                          </span>
                        )}
                      </div>
                    </Card.Body>
                  </Card>
                </Link>
              ))}
              {totalCount > posts.length && (
                <Link to="/blog" className="show-all-card">
                  <div className="show-all-content">
                    <p>Show All</p>
                    <span className="show-all-count">{totalCount} Articles</span>
                  </div>
                </Link>
              )}
            </div>
            <button
              className="slider-btn slider-btn-right"
              onClick={() => scroll('right')}
              disabled={!canScrollRight}
              aria-label="Scroll right"
            >
              →
            </button>
          </div>
        )}
      </section>
    </div>
  );
}

export default HomePage;
