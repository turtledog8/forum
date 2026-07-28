package org.example.forum.service;

import java.util.List;
import org.example.forum.model.Tag;

public interface TagService {

    Tag findOrCreate(String name);

    List<Tag> findAll();

    Tag getById(Long id);

    void delete(Long id);
}
