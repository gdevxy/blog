import React, { useState } from 'react';
import { blogApi } from '@services/api';
import './CommentForm.css';

interface CommentFormProps {
  postId: string;
  commentId?: number;
  isReply?: boolean;
  onCommentAdded: () => void;
  onCancel?: () => void;
}

declare global {
  interface Window {
    grecaptcha: {
      execute: (siteKey: string, options: { action: string }) => Promise<string>;
    };
  }
}

const RECAPTCHA_SITE_KEY = '6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid';
const MAX_COMMENT_LENGTH = 2000;

function CommentForm({ postId, commentId, isReply = false, onCommentAdded, onCancel }: CommentFormProps) {
  const [author, setAuthor] = useState('');
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleCommentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value;
    if (value.length <= MAX_COMMENT_LENGTH) {
      setComment(value);
    }
  };

  const charsRemaining = MAX_COMMENT_LENGTH - comment.length;
  const isNearLimit = charsRemaining < 100;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!comment.trim()) {
      setError('Please enter a comment');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      setSuccess(false);

      if (!window.grecaptcha) {
        setError('reCAPTCHA not loaded');
        return;
      }

      const action = isReply ? 'comment_reply' : 'comment_post';
      const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action });

      if (!token) {
        setError('Failed to get reCAPTCHA token');
        return;
      }

      const commentAction = {
        author: author.trim() || null,
        comment: comment.trim(),
        captcha: token,
        action,
      };

      if (isReply && commentId) {
        await blogApi.addCommentReply(postId, commentId, commentAction as any);
      } else {
        await blogApi.addComment(postId, commentAction as any);
      }

      setAuthor('');
      setComment('');
      setSuccess(true);
      onCommentAdded();

      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to post comment';
      setError(message);
      console.error('Error posting comment:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="comment-form" onSubmit={handleSubmit}>
      <h4 className="comment-form-title">
        {isReply ? 'Write a Reply' : 'Leave a Comment'}
      </h4>

      <div className="form-group">
        <input
          type="text"
          className="form-control"
          placeholder="Your name (optional)"
          value={author}
          onChange={(e) => setAuthor(e.target.value)}
          disabled={loading}
        />
      </div>

      <div className="form-group">
        <textarea
          className="form-control"
          placeholder="Your comment..."
          rows={4}
          value={comment}
          onChange={handleCommentChange}
          disabled={loading}
        />
        <div className={`char-counter ${isNearLimit ? 'char-limit-warning' : ''}`}>
          {charsRemaining} characters remaining
        </div>
      </div>

      {error && <div className="alert alert-danger alert-sm">{error}</div>}
      {success && <div className="alert alert-success alert-sm">Comment posted successfully!</div>}

      <div className="form-actions">
        <button
          type="submit"
          className="btn btn-primary"
          disabled={loading}
        >
          {loading ? 'Publishing...' : 'Publish'}
        </button>
        {isReply && onCancel && (
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onCancel}
            disabled={loading}
          >
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}

export default CommentForm;
