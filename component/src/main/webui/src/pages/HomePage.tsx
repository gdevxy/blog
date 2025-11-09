import React, { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
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
        {loading && <p className="loading-text">Loading articles...</p>}
        {error && <p className="error">Failed to load articles: {error.message}</p>}
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
                <Link key={post.slug} to={`/blog/${post.slug}`} className="post-card post-card-link">
                  {post.image && (
                    <img src={post.image.url} alt={post.title} className="post-image" />
                  )}
                  <h3>{post.title}</h3>
                  {post.description && <p>{post.description}</p>}
                  <div className="post-meta">
                    <span className="date">
                      {new Date(post.publishedDate).toLocaleDateString()}
                    </span>
                    {post.rating && (
                      <span className="likes">
                        👍 {post.rating}
                      </span>
                    )}
                  </div>
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
