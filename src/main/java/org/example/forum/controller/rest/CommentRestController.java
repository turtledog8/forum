package org.example.forum.controller.rest;

import jakarta.validation.Valid;
import org.example.forum.dto.CommentRequest;
import org.example.forum.dto.CommentResponse;
import org.example.forum.security.ForumUserDetails;
import org.example.forum.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PutMapping("/{id}")
    public CommentResponse update(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        return CommentResponse.from(
                commentService.update(id, principal.getId(), principal.isAdmin(), request.content()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal ForumUserDetails principal, @PathVariable Long id) {
        commentService.delete(id, principal.getId(), principal.isAdmin());
    }
}
