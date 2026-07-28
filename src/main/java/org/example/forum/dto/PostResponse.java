package org.example.forum.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.example.forum.model.Post;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorUsername,
        String authorProfilePhotoUrl,
        List<String> tags,
        long likeCount,
        long commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static PostResponse from(Post post, long likeCount, long commentCount) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getAuthor().getProfilePhotoUrl(),
                post.getTags().stream().map(org.example.forum.model.Tag::getName).sorted().toList(),
                likeCount,
                commentCount,
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
