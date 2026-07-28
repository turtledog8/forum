package org.example.forum.controller.rest;

import jakarta.validation.Valid;
import org.example.forum.dto.PostResponse;
import org.example.forum.dto.UserProfileUpdateRequest;
import org.example.forum.dto.UserResponse;
import org.example.forum.model.Post;
import org.example.forum.model.User;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.example.forum.service.FileStorageService;
import org.example.forum.service.LikeService;
import org.example.forum.service.PostService;
import org.example.forum.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final FileStorageService fileStorageService;

    public UserRestController(UserService userService, PostService postService, CommentService commentService,
            LikeService likeService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal ForumUserDetails principal) {
        return UserResponse.from(principal.getUser());
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal ForumUserDetails principal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        User updates = new User();
        updates.setFirstName(request.firstName());
        updates.setLastName(request.lastName());
        updates.setEmail(request.email());
        updates.setPhoneNumber(request.phoneNumber());
        updates.setProfilePhotoUrl(request.profilePhotoUrl());
        return UserResponse.from(userService.updateProfile(principal.getId(), updates));
    }

    @PostMapping("/me/photo")
    public UserResponse uploadPhoto(@AuthenticationPrincipal ForumUserDetails principal,
            @RequestParam MultipartFile photo) {
        User updates = new User();
        updates.setFirstName(principal.getUser().getFirstName());
        updates.setLastName(principal.getUser().getLastName());
        updates.setProfilePhotoUrl(fileStorageService.store(photo, "profile-photos"));
        return UserResponse.from(userService.updateProfile(principal.getId(), updates));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@AuthenticationPrincipal ForumUserDetails principal) {
        userService.delete(principal.getId());
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return UserResponse.from(userService.getById(id));
    }

    @GetMapping("/{id}/posts")
    public Page<PostResponse> posts(@PathVariable Long id, Pageable pageable) {
        return postService.findByAuthor(id, pageable).map(this::toResponse);
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.from(post, likeService.countLikes(post.getId()), commentService.countByPost(post.getId()));
    }
}
