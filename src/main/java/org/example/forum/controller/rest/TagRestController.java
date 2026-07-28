package org.example.forum.controller.rest;

import java.util.List;
import org.example.forum.dto.TagResponse;
import org.example.forum.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagRestController {

    private final TagService tagService;

    public TagRestController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public List<TagResponse> list() {
        return tagService.findAll().stream().map(TagResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TagResponse get(@PathVariable Long id) {
        return TagResponse.from(tagService.getById(id));
    }
}
