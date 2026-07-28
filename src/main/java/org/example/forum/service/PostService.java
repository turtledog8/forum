package org.example.forum.service;

import java.util.Collection;
import java.util.List;
import org.example.forum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Post create(Long authorId, Post post);

    Post getById(Long id);

    Page<Post> findAll(String search, Pageable pageable);

    Page<Post> findByAuthor(Long authorId, Pageable pageable);

    List<Post> findTop10Recent();

    List<Post> findTop10MostCommented();

    List<Post> findRecentByTags(Collection<String> tagNames, int limit);

    List<Post> findMostCommentedByTags(Collection<String> tagNames, int limit);

    Post update(Long postId, Long requesterId, boolean requesterIsAdmin, Post updates);

    void delete(Long postId, Long requesterId, boolean requesterIsAdmin);

    Post addTag(Long postId, Long requesterId, boolean requesterIsAdmin, String tagName);

    Post removeTag(Long postId, Long requesterId, boolean requesterIsAdmin, String tagName);

    List<Post> findByTag(String tagName);

    Page<Post> findByCategoryTags(Collection<String> tagNames, Pageable pageable);

    long countPosts();
}
