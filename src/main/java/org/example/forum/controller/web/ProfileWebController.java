package org.example.forum.controller.web;

import org.example.forum.dto.PostResponse;
import org.example.forum.dto.UserResponse;
import org.example.forum.model.Post;
import org.example.forum.model.User;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.example.forum.service.FileStorageService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.example.forum.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProfileWebController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final FileStorageService fileStorageService;

    public ProfileWebController(UserService userService, PostService postService, CommentService commentService,
            LikeService likeService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/profile")
    public String ownProfile(@AuthenticationPrincipal ForumUserDetails principal, Model model) {
        populate(principal.getId(), true, model);
        return "profile/view";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal ForumUserDetails principal,
            @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) MultipartFile photo) {
        User updates = new User();
        updates.setFirstName(firstName);
        updates.setLastName(lastName);
        updates.setEmail(email);
        updates.setPhoneNumber(phoneNumber);
        if (photo != null && !photo.isEmpty()) {
            updates.setProfilePhotoUrl(fileStorageService.store(photo, "profile-photos"));
        }
        userService.updateProfile(principal.getId(), updates);
        return "redirect:/profile";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, @AuthenticationPrincipal ForumUserDetails principal, Model model) {
        if (principal != null && principal.getId().equals(id)) {
            return "redirect:/profile";
        }
        populate(id, false, model);
        return "profile/view";
    }

    private void populate(Long userId, boolean editable, Model model) {
        model.addAttribute("profile", UserResponse.from(userService.getById(userId)));
        model.addAttribute("editable", editable);
        model.addAttribute("posts", postService.findByAuthor(userId, Pageable.unpaged())
                .map(this::toResponse).getContent());
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, likeService.countLikes(post.getId()), commentService.countByPost(post.getId()));
    }
}
