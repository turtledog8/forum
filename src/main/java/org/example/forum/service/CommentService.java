package org.example.forum.service;

import java.util.List;
import org.example.forum.model.Comment;

public interface CommentService {

    Comment addComment(Long postId, Long authorId, String content);

    List<Comment> findByPost(Long postId);

    long countByPost(Long postId);

    Comment update(Long commentId, Long requesterId, boolean requesterIsAdmin, String content);

    void delete(Long commentId, Long requesterId, boolean requesterIsAdmin);
}
