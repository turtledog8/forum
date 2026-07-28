package org.example.forum.dto;

import java.time.LocalDateTime;
import org.example.forum.model.Comment;

public record CommentResponse(
        Long id,
        Long postId,
        String authorUsername,
        String authorProfilePhotoUrl,
        String content,
        LocalDateTime createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getUsername(),
                comment.getAuthor().getProfilePhotoUrl(),
                comment.getContent(),
                comment.getCreatedAt());
    }
}
