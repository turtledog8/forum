package org.example.forum.controller.rest;

import java.util.List;
import java.util.Map;
import org.example.forum.dto.PostResponse;
import org.example.forum.model.Post;
import org.example.forum.service.CommentService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.example.forum.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicRestController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PublicRestController(UserService userService, PostService postService, CommentService commentService,
            LikeService likeService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "userCount", userService.countUsers(),
                "postCount", postService.countPosts());
    }

    @GetMapping("/posts/recent")
    public List<PostResponse> recentPosts() {
        return postService.findTop10Recent().stream().map(this::toResponse).toList();
    }

    @GetMapping("/posts/most-commented")
    public List<PostResponse> mostCommentedPosts() {
        return postService.findTop10MostCommented().stream().map(this::toResponse).toList();
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, likeService.countLikes(post.getId()), commentService.countByPost(post.getId()));
    }
}
