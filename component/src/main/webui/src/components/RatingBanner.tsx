import React, { useState, useRef } from 'react';
import { blogApi } from '@services/api';
import 'bootstrap-icons/font/bootstrap-icons.css';
import './RatingBanner.css';

interface RatingBannerProps {
  postId: string;
  liked?: boolean;
}

declare global {
  interface Window {
    grecaptcha: {
      execute: (siteKey: string, options: { action: string }) => Promise<string>;
    };
  }
}

const RECAPTCHA_SITE_KEY = '6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid';

function RatingBanner({ postId, liked: initialLiked = false }: RatingBannerProps) {
  const [loading, setLoading] = useState(false);
  const [liked, setLiked] = useState(initialLiked);
  const [error, setError] = useState<string | null>(null);
  const [isAnimating, setIsAnimating] = useState(false);
  const [sparks, setSparks] = useState<Array<{ id: number; tx: number; ty: number }>>([]);
  const buttonRef = useRef<HTMLButtonElement>(null);
  const bannerRef = useRef<HTMLDivElement>(null);
  let sparkId = 0;

  const triggerAnimation = () => {
    setIsAnimating(true);

    // Create sparks
    const newSparks = [];
    for (let i = 0; i < 8; i++) {
      const angle = (i / 8) * Math.PI * 2;
      const distance = 60;
      const tx = Math.cos(angle) * distance;
      const ty = Math.sin(angle) * distance;
      newSparks.push({ id: sparkId++, tx, ty });
    }
    setSparks(newSparks);

    // Remove animation class after animation completes
    setTimeout(() => {
      setIsAnimating(false);
      setSparks([]);
    }, 600);
  };

  const handleThumbsUp = async () => {
    try {
      setLoading(true);
      setError(null);
      triggerAnimation();

      if (liked) {
        // If already liked, remove the rating by calling thumbs down
        await blogApi.thumbsDown(postId);
        setLiked(false);
      } else {
        // If not liked, add the rating by calling thumbs up with reCAPTCHA
        if (!window.grecaptcha) {
          setError('reCAPTCHA not loaded');
          return;
        }

        // Get the reCAPTCHA token
        const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action: 'thumbs_up' });

        if (!token) {
          setError('Failed to get reCAPTCHA token');
          return;
        }

        // Call the thumbs up endpoint
        await blogApi.thumbsUp(postId, {
          captcha: token,
          action: 'thumbs_up',
        });

        setLiked(true);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to update rating';
      setError(message);
      console.error('Error updating rating:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="rating-banner" ref={bannerRef}>
      <div className="rating-content">
        <span className="rating-text">
          <span className="d-inline d-sm-none">Leave a thumbs up</span>
          <span className="d-none d-sm-inline">Leave a thumbs up if you liked it</span>
        </span>
        <button
          ref={buttonRef}
          className={`rating-button ${liked ? 'liked' : ''} ${isAnimating ? 'animating' : ''}`}
          onClick={handleThumbsUp}
          disabled={loading}
          title={liked ? 'Remove your rating' : 'Give this post a thumbs up'}
          type="button"
        >
          <i className="bi bi-hand-thumbs-up-fill"></i>
          {sparks.map((spark) => (
            <div
              key={spark.id}
              className="spark animating"
              style={
                {
                  '--tx': `${spark.tx}px`,
                  '--ty': `${spark.ty}px`,
                  left: '50%',
                  top: '50%',
                  transform: 'translate(-50%, -50%)',
                } as React.CSSProperties
              }
            />
          ))}
        </button>
      </div>
      {error && <div className="rating-error">{error}</div>}
    </div>
  );
}

export default RatingBanner;
