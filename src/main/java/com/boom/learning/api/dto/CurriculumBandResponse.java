package com.boom.learning.api.dto;

public record CurriculumBandResponse(String id, String frameworkId, String code, int minAgeMonths, int maxAgeMonths, String gradeLevel, String schoolStage, int displayOrder, String status) {}
