package org.example.forum.controller.rest;

import org.example.forum.dto.UserResponse;
import org.example.forum.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/users")
public class AdminRestController {

    private final UserService userService;

    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponse> search(@RequestParam(required = false) String query, Pageable pageable) {
        return userService.search(query, pageable).map(UserResponse::from);
    }

    @PatchMapping("/{id}/block")
    public UserResponse block(@PathVariable Long id) {
        userService.setBlocked(id, true);
        return UserResponse.from(userService.getById(id));
    }

    @PatchMapping("/{id}/unblock")
    public UserResponse unblock(@PathVariable Long id) {
        userService.setBlocked(id, false);
        return UserResponse.from(userService.getById(id));
    }

    @PatchMapping("/{id}/promote")
    public UserResponse promote(@PathVariable Long id) {
        return UserResponse.from(userService.promoteToAdmin(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
