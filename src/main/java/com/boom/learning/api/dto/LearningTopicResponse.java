package com.boom.learning.api.dto;

public record LearningTopicResponse(String id, String subjectId, String code, String defaultName, String description, int displayOrder, String status) {}
