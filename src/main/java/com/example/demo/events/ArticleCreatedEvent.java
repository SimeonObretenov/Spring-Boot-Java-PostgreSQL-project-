package com.example.demo.events;

public record ArticleCreatedEvent(Long articleId, String ownerUsername) {}
