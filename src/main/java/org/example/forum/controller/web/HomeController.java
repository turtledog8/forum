package org.example.forum.controller.web;

import java.util.Arrays;
import java.util.List;
import org.example.forum.model.ForumCategory;
import org.example.forum.model.Post;
import org.example.forum.service.PostService;
import org.example.forum.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final int POSTS_PER_CATEGORY = 5;

    private final UserService userService;
    private final PostService postService;

    public HomeController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("postCount", postService.countPosts());
        model.addAttribute("recentPosts", postService.findTop10Recent());
        model.addAttribute("mostCommentedPosts", postService.findTop10MostCommented());

        List<CategorySection> sections = Arrays.stream(ForumCategory.values())
                .map(category -> new CategorySection(
                        category.name(),
                        category.getDisplayName(),
                        postService.findRecentByTags(category.getTags(), POSTS_PER_CATEGORY),
                        postService.findMostCommentedByTags(category.getTags(), POSTS_PER_CATEGORY)))
                .toList();
        model.addAttribute("categorySections", sections);

        return "index";
    }

    public record CategorySection(String key, String label, List<Post> recentPosts, List<Post> topCommentedPosts) {
    }
}
