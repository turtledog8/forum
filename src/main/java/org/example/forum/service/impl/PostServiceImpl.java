package org.example.forum.service.impl;

import java.util.Collection;
import java.util.List;
import org.example.forum.exception.ForbiddenActionException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Post;
import org.example.forum.model.Tag;
import org.example.forum.model.User;
import org.example.forum.repository.PostRepository;
import org.example.forum.repository.UserRepository;
import org.example.forum.service.PostService;
import org.example.forum.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagService = tagService;
    }

    @Override
    @Transactional
    public Post create(Long authorId, Post post) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorId));
        if (author.isBlocked()) {
            throw new ForbiddenActionException("Blocked users cannot create posts");
        }
        post.setId(null);
        post.setAuthor(author);
        return postRepository.save(post);
    }

    @Override
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
    }

    @Override
    public Page<Post> findAll(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return postRepository.findAll(pageable);
        }
        String like = "%" + search.toLowerCase() + "%";
        Specification<Post> spec = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("content")), like));
        return postRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Post> findByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable);
    }

    @Override
    public List<Post> findTop10Recent() {
        return postRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Post> findTop10MostCommented() {
        return postRepository.findMostCommented(PageRequest.of(0, 10));
    }

    @Override
    public List<Post> findRecentByTags(Collection<String> tagNames, int limit) {
        return postRepository.findDistinctByTags_NameInOrderByCreatedAtDesc(tagNames, PageRequest.of(0, limit));
    }

    @Override
    public List<Post> findMostCommentedByTags(Collection<String> tagNames, int limit) {
        return postRepository.findMostCommentedByTags(tagNames, PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public Post update(Long postId, Long requesterId, boolean requesterIsAdmin, Post updates) {
        Post post = getById(postId);
        requireOwnerOrAdmin(post, requesterId, requesterIsAdmin);
        post.setTitle(updates.getTitle());
        post.setContent(updates.getContent());
        return post;
    }

    @Override
    @Transactional
    public void delete(Long postId, Long requesterId, boolean requesterIsAdmin) {
        Post post = getById(postId);
        requireOwnerOrAdmin(post, requesterId, requesterIsAdmin);
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public Post addTag(Long postId, Long requesterId, boolean requesterIsAdmin, String tagName) {
        Post post = getById(postId);
        requireOwnerOrAdmin(post, requesterId, requesterIsAdmin);
        Tag tag = tagService.findOrCreate(tagName);
        post.getTags().add(tag);
        return post;
    }

    @Override
    @Transactional
    public Post removeTag(Long postId, Long requesterId, boolean requesterIsAdmin, String tagName) {
        Post post = getById(postId);
        requireOwnerOrAdmin(post, requesterId, requesterIsAdmin);
        post.getTags().removeIf(tag -> tag.getName().equalsIgnoreCase(tagName));
        return post;
    }

    @Override
    public List<Post> findByTag(String tagName) {
        return postRepository.findByTags_NameIgnoreCase(tagName);
    }

    @Override
    public Page<Post> findByCategoryTags(Collection<String> tagNames, Pageable pageable) {
        return postRepository.findDistinctByTags_NameIn(tagNames, pageable);
    }

    @Override
    public long countPosts() {
        return postRepository.count();
    }

    private void requireOwnerOrAdmin(Post post, Long requesterId, boolean requesterIsAdmin) {
        if (requesterIsAdmin) {
            return;
        }
        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenActionException("You can only modify your own posts");
        }
    }
}
