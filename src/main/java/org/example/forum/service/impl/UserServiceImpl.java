package org.example.forum.service.impl;

import org.example.forum.exception.DuplicateResourceException;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Role;
import org.example.forum.model.User;
import org.example.forum.repository.UserRepository;
import org.example.forum.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + user.getEmail());
        }
        user.setRole(Role.USER);
        user.setBlocked(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public Page<User> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll(pageable);
        }
        String like = "%" + query.toLowerCase() + "%";
        Specification<User> spec = (root, criteriaQuery, cb) -> cb.or(
                cb.like(cb.lower(root.get("username")), like),
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("firstName")), like));
        return userRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public User updateProfile(Long id, User updates) {
        User user = getById(id);
        user.setFirstName(updates.getFirstName());
        user.setLastName(updates.getLastName());
        if (updates.getPhoneNumber() != null) {
            user.setPhoneNumber(updates.getPhoneNumber());
        }
        if (updates.getProfilePhotoUrl() != null) {
            user.setProfilePhotoUrl(updates.getProfilePhotoUrl());
        }
        if (updates.getEmail() != null && !updates.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updates.getEmail())) {
                throw new DuplicateResourceException("Email already registered: " + updates.getEmail());
            }
            user.setEmail(updates.getEmail());
        }
        return user;
    }

    @Override
    @Transactional
    public void setBlocked(Long id, boolean blocked) {
        User user = getById(id);
        user.setBlocked(blocked);
    }

    @Override
    @Transactional
    public User promoteToAdmin(Long id) {
        User user = getById(id);
        user.setRole(Role.ADMIN);
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }
}
