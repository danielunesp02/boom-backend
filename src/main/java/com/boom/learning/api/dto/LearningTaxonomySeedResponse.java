package com.boom.learning.api.dto;
import java.util.*;
public record LearningTaxonomySeedResponse(int subjects,int topics,int skills,int objectives,int frameworks,int bands,int expectations,Map<String,UUID> references) {}
