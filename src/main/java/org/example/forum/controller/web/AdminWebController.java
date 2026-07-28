package org.example.forum.controller.web;

import org.example.forum.dto.PostResponse;
import org.example.forum.dto.UserResponse;
import org.example.forum.model.Post;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.example.forum.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public AdminWebController(UserService userService, PostService postService, CommentService commentService,
            LikeService likeService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String query, Model model, Pageable pageable) {
        Page<UserResponse> users = userService.search(query, pageable).map(UserResponse::from);
        model.addAttribute("query", query);
        model.addAttribute("users", users.getContent());
        model.addAttribute("page", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/block")
    public String block(@PathVariable Long id) {
        userService.setBlocked(id, true);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblock(@PathVariable Long id) {
        userService.setBlocked(id, false);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/promote")
    public String promote(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/posts")
    public String posts(@RequestParam(required = false) String search, Model model, Pageable pageable) {
        Page<Post> postsPage = postService.findAll(search, pageable);
        model.addAttribute("search", search);
        model.addAttribute("posts", postsPage.getContent().stream().map(this::toResponse).toList());
        model.addAttribute("page", postsPage);
        return "admin/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal) {
        postService.delete(id, principal.getId(), true);
        return "redirect:/admin/posts";
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, likeService.countLikes(post.getId()), commentService.countByPost(post.getId()));
    }
}
