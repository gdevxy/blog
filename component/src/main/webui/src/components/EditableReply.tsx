import React, { useState } from 'react';
import { format } from 'date-fns';
import { blogApi } from '@services/api';
import { BlogPostComment, BlogPostCommentAction } from '@types/api';
import './EditableReply.css';

declare global {
  interface Window {
    grecaptcha: {
      execute: (siteKey: string, options: { action: string }) => Promise<string>;
    };
  }
}

const RECAPTCHA_SITE_KEY = '6Lc7vagqAAAAAKi_E_E275yxYo_B80-RvOVmVaid';

interface EditableReplyProps {
  reply: BlogPostComment;
  onReplyUpdated: () => void;
}

function EditableReply({ reply, onReplyUpdated }: EditableReplyProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editedComment, setEditedComment] = useState(reply.comment);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleEdit = async () => {
    if (!editedComment.trim()) {
      setError('Reply cannot be empty');
      return;
    }

    if (editedComment === reply.comment) {
      setIsEditing(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      if (!window.grecaptcha) {
        setError('reCAPTCHA not loaded');
        return;
      }

      const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action: 'comment_update' });

      if (!token) {
        setError('Failed to get reCAPTCHA token');
        return;
      }

      const updateData: any = {
        author: reply.author,
        comment: editedComment.trim(),
        captcha: token,
        action: 'comment_update',
      };

      await blogApi.updateCommentReply(reply.id, updateData);
      setIsEditing(false);
      onReplyUpdated();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to update reply';
      setError(message);
      console.error('Error updating reply:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setEditedComment(reply.comment);
    setError(null);
    setIsEditing(false);
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this reply?')) {
      return;
    }

    try {
      setLoading(true);
      setError(null);

      if (!window.grecaptcha) {
        setError('reCAPTCHA not loaded');
        return;
      }

      const token = await window.grecaptcha.execute(RECAPTCHA_SITE_KEY, { action: 'comment_delete' });

      if (!token) {
        setError('Failed to get reCAPTCHA token');
        return;
      }

      const deleteData: any = {
        captcha: token,
        action: 'comment_delete',
      };

      await blogApi.deleteCommentReply(reply.id, deleteData);
      onReplyUpdated();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete reply';
      setError(message);
      console.error('Error deleting reply:', err);
    } finally {
      setLoading(false);
    }
  };

  if (isEditing) {
    return (
      <li className="reply">
        <div className="reply-header">
          <strong>{reply.author || 'Anonymous'}</strong>
          <div className="reply-header-actions">
            <time>{format(new Date(reply.createdAt), 'yyyy-MM-dd \'at\' h:mm a')}</time>
          </div>
        </div>
        <textarea
          className="edit-textarea-reply"
          value={editedComment}
          onChange={(e) => setEditedComment(e.target.value)}
          disabled={loading}
          rows={3}
        />
        {error && <div className="alert alert-danger alert-sm">{error}</div>}
        <div className="edit-actions-reply">
          <button
            className="btn btn-sm btn-primary"
            onClick={handleEdit}
            disabled={loading}
          >
            {loading ? 'Saving...' : 'Save'}
          </button>
          <button
            className="btn btn-sm btn-secondary"
            onClick={handleCancel}
            disabled={loading}
          >
            Cancel
          </button>
        </div>
      </li>
    );
  }

  return (
    <li className="reply">
      <div className="reply-header">
        <strong>{reply.author || 'Anonymous'}</strong>
        <div className="reply-header-actions">
          <time>{format(new Date(reply.createdAt), 'yyyy-MM-dd \'at\' h:mm a')}</time>
          {reply.modifiable && (
            <>
              <button
                className="reply-edit-btn"
                onClick={() => setIsEditing(true)}
                title="Edit reply"
                disabled={loading}
              >
                <i className="bi bi-pencil-square"></i>
              </button>
              <button
                className="reply-delete-btn"
                onClick={handleDelete}
                title="Delete reply"
                disabled={loading}
              >
                <i className="bi bi-trash3-fill"></i>
              </button>
            </>
          )}
        </div>
      </div>
      {error && <div className="alert alert-danger alert-sm">{error}</div>}
      <p>{reply.comment}</p>
    </li>
  );
}

export default EditableReply;
