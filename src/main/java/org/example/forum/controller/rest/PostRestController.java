package org.example.forum.controller.rest;

import jakarta.validation.Valid;
import java.util.List;
import org.example.forum.dto.CommentRequest;
import org.example.forum.dto.CommentResponse;
import org.example.forum.dto.PostRequest;
import org.example.forum.dto.PostResponse;
import org.example.forum.model.Post;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostRestController(PostService postService, CommentService commentService, LikeService likeService) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping
    public Page<PostResponse> list(@RequestParam(required = false) String search, Pageable pageable) {
        return postService.findAll(search, pageable).map(this::toResponse);
    }

    @GetMapping("/by-tag/{tagName}")
    public List<PostResponse> byTag(@PathVariable String tagName) {
        return postService.findByTag(tagName).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return toResponse(postService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@AuthenticationPrincipal ForumUserDetails principal,
            @Valid @RequestBody PostRequest request) {
        Post post = new Post();
        post.setTitle(request.title());
        post.setContent(request.content());
        return toResponse(postService.create(principal.getId(), post));
    }

    @PutMapping("/{id}")
    public PostResponse update(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {
        Post updates = new Post();
        updates.setTitle(request.title());
        updates.setContent(request.content());
        return toResponse(postService.update(id, principal.getId(), principal.isAdmin(), updates));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id) {
        postService.delete(id, principal.getId(), principal.isAdmin());
    }

    @PostMapping("/{id}/tags/{tagName}")
    public PostResponse addTag(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id,
            @PathVariable String tagName) {
        return toResponse(postService.addTag(id, principal.getId(), principal.isAdmin(), tagName));
    }

    @DeleteMapping("/{id}/tags/{tagName}")
    public PostResponse removeTag(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id,
            @PathVariable String tagName) {
        return toResponse(postService.removeTag(id, principal.getId(), principal.isAdmin(), tagName));
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponse> comments(@PathVariable Long id) {
        return commentService.findByPost(id).stream().map(CommentResponse::from).toList();
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        return CommentResponse.from(commentService.addComment(id, principal.getId(), request.content()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Long> toggleLike(@AuthenticationPrincipal ForumUserDetails principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(likeService.toggleLike(id, principal.getId()));
    }

    private PostResponse toResponse(Post post) {
        long likeCount = likeService.countLikes(post.getId());
        long commentCount = commentService.countByPost(post.getId());
        return PostResponse.from(post, likeCount, commentCount);
    }
}
