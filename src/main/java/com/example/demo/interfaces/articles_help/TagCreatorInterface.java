package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.TagCreateRequest;
import com.example.demo.entity.Tag;

public interface TagCreatorInterface {
    Tag create(TagCreateRequest req);
}
