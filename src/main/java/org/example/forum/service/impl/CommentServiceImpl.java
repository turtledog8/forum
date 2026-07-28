package org.example.forum.service.impl;

import java.util.List;
import org.example.forum.exception.ForbiddenActionException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Comment;
import org.example.forum.model.Post;
import org.example.forum.model.User;
import org.example.forum.repository.CommentRepository;
import org.example.forum.repository.PostRepository;
import org.example.forum.repository.UserRepository;
import org.example.forum.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Comment addComment(Long postId, Long authorId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorId));
        if (author.isBlocked()) {
            throw new ForbiddenActionException("Blocked users cannot comment");
        }
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    public long countByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    @Transactional
    public Comment update(Long commentId, Long requesterId, boolean requesterIsAdmin, String content) {
        Comment comment = getById(commentId);
        requireOwnerOrAdmin(comment, requesterId, requesterIsAdmin);
        comment.setContent(content);
        return comment;
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long requesterId, boolean requesterIsAdmin) {
        Comment comment = getById(commentId);
        requireOwnerOrAdmin(comment, requesterId, requesterIsAdmin);
        commentRepository.delete(comment);
    }

    private Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
    }

    private void requireOwnerOrAdmin(Comment comment, Long requesterId, boolean requesterIsAdmin) {
        if (requesterIsAdmin) {
            return;
        }
        if (!comment.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenActionException("You can only modify your own comments");
        }
    }
}
