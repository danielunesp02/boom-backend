package com.boom.learning.api.dto;

public record LearningObjectiveResponse(String id, String skillId, String code, String description, String complexityLevel, String depthLevel, int displayOrder, String status) {}
