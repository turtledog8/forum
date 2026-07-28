package org.example.forum.dto;

import org.example.forum.model.Tag;

public record TagResponse(Long id, String name) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
