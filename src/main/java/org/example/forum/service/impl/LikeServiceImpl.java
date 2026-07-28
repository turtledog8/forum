package org.example.forum.service.impl;

import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Post;
import org.example.forum.model.PostLike;
import org.example.forum.model.User;
import org.example.forum.repository.PostLikeRepository;
import org.example.forum.repository.PostRepository;
import org.example.forum.repository.UserRepository;
import org.example.forum.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(PostLikeRepository postLikeRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public long toggleLike(Long postId, Long userId) {
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        } else {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            postLikeRepository.save(new PostLike(post, user));
        }
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
