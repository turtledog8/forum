package org.example.forum.service;

import org.example.forum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User register(User user);

    User getById(Long id);

    User getByUsername(String username);

    Page<User> search(String query, Pageable pageable);

    User updateProfile(Long id, User updates);

    void setBlocked(Long id, boolean blocked);

    User promoteToAdmin(Long id);

    void delete(Long id);

    long countUsers();
}
