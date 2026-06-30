package com.boom.learning.api.dto;

public record CurriculumExpectationResponse(String id, String bandId, String subjectId, String topicId, String skillId, String objectiveId, String expectedKnowledgeLevel, String expectedComplexityLevel, String expectedDepthLevel, String priority, String status) {}
