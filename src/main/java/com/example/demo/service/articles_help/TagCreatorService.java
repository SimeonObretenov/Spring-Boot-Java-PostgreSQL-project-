package com.example.demo.service.articles_help;

import com.example.demo.dto.TagCreateRequest;
import com.example.demo.entity.Tag;
import com.example.demo.interfaces.articles_help.TagCreatorInterface;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagCreatorService implements TagCreatorInterface {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public Tag create(TagCreateRequest req) {
        String name = req.name().trim();
        if (tagRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Tag with name '" + name + "' already exists.");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }
}
