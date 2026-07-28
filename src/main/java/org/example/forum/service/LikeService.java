package org.example.forum.service;

public interface LikeService {

    long toggleLike(Long postId, Long userId);

    boolean hasLiked(Long postId, Long userId);

    long countLikes(Long postId);
}
