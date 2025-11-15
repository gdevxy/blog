import React, { useState } from 'react';
import { format } from 'date-fns';
import { blogApi } from '@services/api';
import { BlogPostComment, BlogPostCommentAction } from '@types/api';
import './EditableComment.css';

interface EditableCommentProps {
  comment: BlogPostComment;
  onCommentUpdated: () => void;
  onReply?: () => void;
  isReplying?: boolean;
}

function EditableComment({ comment, onCommentUpdated, onReply, isReplying }: EditableCommentProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editedComment, setEditedComment] = useState(comment.comment);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleEdit = async () => {
    if (!editedComment.trim()) {
      setError('Comment cannot be empty');
      return;
    }

    if (editedComment === comment.comment) {
      setIsEditing(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const updateData: BlogPostCommentAction = {
        author: comment.author,
        comment: editedComment.trim(),
      };

      await blogApi.updateComment(comment.id, updateData);
      setIsEditing(false);
      onCommentUpdated();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to update comment';
      setError(message);
      console.error('Error updating comment:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setEditedComment(comment.comment);
    setError(null);
    setIsEditing(false);
  };

  if (isEditing) {
    return (
      <div className="comment">
        <div className="comment-header">
          <strong>{comment.author || 'Anonymous'}</strong>
          <div className="comment-header-actions">
            <time>{format(new Date(comment.createdAt), 'yyyy-MM-dd \'at\' h:mm a')}</time>
          </div>
        </div>
        <textarea
          className="edit-textarea"
          value={editedComment}
          onChange={(e) => setEditedComment(e.target.value)}
          disabled={loading}
          rows={4}
        />
        {error && <div className="alert alert-danger alert-sm">{error}</div>}
        <div className="edit-actions">
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
      </div>
    );
  }

  return (
    <div className="comment">
      <div className="comment-header">
        <strong>{comment.author || 'Anonymous'}</strong>
        <div className="comment-header-actions">
          <time>{format(new Date(comment.createdAt), 'yyyy-MM-dd \'at\' h:mm a')}</time>
          <button
            className="comment-reply-btn"
            onClick={onReply}
            title={isReplying ? 'Cancel reply' : 'Reply to comment'}
          >
            <i className="bi bi-reply-fill"></i>
          </button>
          {comment.modifiable && (
            <button
              className="comment-edit-btn"
              onClick={() => setIsEditing(true)}
              title="Edit comment"
            >
              <i className="bi bi-pencil-square"></i>
            </button>
          )}
        </div>
      </div>
      <p>{comment.comment}</p>
    </div>
  );
}

export default EditableComment;
