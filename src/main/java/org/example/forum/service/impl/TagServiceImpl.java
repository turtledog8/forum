package org.example.forum.service.impl;

import java.util.List;
import org.example.forum.exception.ResourceNotFoundException;
import org.example.forum.model.Tag;
import org.example.forum.repository.TagRepository;
import org.example.forum.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Tag findOrCreate(String name) {
        String normalized = name.trim().toLowerCase();
        return tagRepository.findByName(normalized)
                .orElseGet(() -> tagRepository.save(new Tag(normalized)));
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found: " + id);
        }
        tagRepository.deleteById(id);
    }
}
