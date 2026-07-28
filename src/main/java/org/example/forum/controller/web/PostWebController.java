package org.example.forum.controller.web;

import jakarta.validation.Valid;
import java.util.List;
import org.example.forum.dto.CommentResponse;
import org.example.forum.dto.PostResponse;
import org.example.forum.exception.ForbiddenActionException;
import org.example.forum.model.ForumCategory;
import org.example.forum.model.Post;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostWebController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostWebController(PostService postService, CommentService commentService, LikeService likeService) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/posts")
    public String list(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag, @RequestParam(required = false) String category,
            Pageable pageable) {
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("category", category);
        model.addAttribute("categories", ForumCategory.values());

        ForumCategory selectedCategory = parseCategory(category);
        model.addAttribute("categoryLabel", selectedCategory != null ? selectedCategory.getDisplayName() : null);

        if (selectedCategory != null) {
            Page<Post> postsPage = postService.findByCategoryTags(selectedCategory.getTags(), pageable);
            model.addAttribute("posts", postsPage.getContent().stream().map(this::toResponse).toList());
            model.addAttribute("page", postsPage);
        } else if (tag != null && !tag.isBlank()) {
            List<PostResponse> posts = postService.findByTag(tag).stream().map(this::toResponse).toList();
            model.addAttribute("posts", posts);
            model.addAttribute("page", null);
        } else {
            Page<Post> postsPage = postService.findAll(search, pageable);
            model.addAttribute("posts", postsPage.getContent().stream().map(this::toResponse).toList());
            model.addAttribute("page", postsPage);
        }
        return "posts/list";
    }

    private ForumCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        for (ForumCategory value : ForumCategory.values()) {
            if (value.name().equals(category)) {
                return value;
            }
        }
        return null;
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal, Model model) {
        Post post = postService.getById(id);
        List<CommentResponse> comments = commentService.findByPost(id).stream().map(CommentResponse::from).toList();
        boolean liked = principal != null && likeService.hasLiked(id, principal.getId());
        boolean canEdit = principal != null
                && (principal.isAdmin() || post.getAuthor().getId().equals(principal.getId()));

        model.addAttribute("post", toResponse(post));
        model.addAttribute("comments", comments);
        model.addAttribute("liked", liked);
        model.addAttribute("canEdit", canEdit);
        return "posts/detail";
    }

    @GetMapping("/posts/new")
    public String newForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("editing", false);
        return "posts/form";
    }

    @PostMapping("/posts")
    public String create(@AuthenticationPrincipal ForumUserDetails principal,
            @Valid @ModelAttribute("post") Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            return "posts/form";
        }
        Post created = postService.create(principal.getId(), post);
        return "redirect:/posts/" + created.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal, Model model) {
        Post post = postService.getById(id);
        requireOwnerOrAdmin(post, principal);
        model.addAttribute("post", post);
        model.addAttribute("editing", true);
        return "posts/form";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal,
            @Valid @ModelAttribute("post") Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", true);
            return "posts/form";
        }
        postService.update(id, principal.getId(), principal.isAdmin(), post);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal) {
        postService.delete(id, principal.getId(), principal.isAdmin());
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal,
            @RequestParam String content) {
        commentService.addComment(id, principal.getId(), content);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/like")
    public String toggleLike(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal) {
        likeService.toggleLike(id, principal.getId());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/tags")
    public String addTag(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal,
            @RequestParam String tagName) {
        postService.addTag(id, principal.getId(), principal.isAdmin(), tagName);
        return "redirect:/posts/" + id + "/edit";
    }

    @PostMapping("/posts/{id}/tags/delete")
    public String removeTag(@PathVariable Long id, @RequestParam String tagName,
            @AuthenticationPrincipal ForumUserDetails principal) {
        postService.removeTag(id, principal.getId(), principal.isAdmin(), tagName);
        return "redirect:/posts/" + id + "/edit";
    }

    private void requireOwnerOrAdmin(Post post, ForumUserDetails principal) {
        if (!principal.isAdmin() && !post.getAuthor().getId().equals(principal.getId())) {
            throw new ForbiddenActionException("You can only edit your own posts");
        }
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, likeService.countLikes(post.getId()), commentService.countByPost(post.getId()));
    }
}
