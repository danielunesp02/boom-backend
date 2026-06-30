package com.boom.learning.api.dto;

public record LearningSkillResponse(String id, String topicId, String code, String defaultName, String description, int displayOrder, String status) {}
