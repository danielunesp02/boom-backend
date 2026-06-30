package com.boom.learning.api;
import com.boom.learning.api.dto.*; import com.boom.learning.application.LearningCatalogService; import com.boom.learning.domain.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/learning") public class LearningCatalogController {
 private final LearningCatalogService service; public LearningCatalogController(LearningCatalogService service){this.service=service;}
 @GetMapping("/subjects") public List<LearningSubjectResponse> subjects(){return service.listSubjects().stream().map(s->new LearningSubjectResponse(s.id().toString(),s.code(),s.defaultName(),s.description(),s.status().name())).toList();}
 @GetMapping("/subjects/{subjectId}/topics") public List<LearningTopicResponse> topics(@PathVariable UUID subjectId){return service.listTopics(subjectId).stream().map(t->new LearningTopicResponse(t.id().toString(),t.subjectId().toString(),t.code(),t.defaultName(),t.description(),t.displayOrder(),t.status().name())).toList();}
 @GetMapping("/topics/{topicId}/skills") public List<LearningSkillResponse> skills(@PathVariable UUID topicId){return service.listSkills(topicId).stream().map(s->new LearningSkillResponse(s.id().toString(),s.topicId().toString(),s.code(),s.defaultName(),s.description(),s.displayOrder(),s.status().name())).toList();}
 @GetMapping("/skills/{skillId}/objectives") public List<LearningObjectiveResponse> objectives(@PathVariable UUID skillId){return service.listObjectives(skillId).stream().map(o->new LearningObjectiveResponse(o.id().toString(),o.skillId().toString(),o.code(),o.description(),o.complexityLevel().name(),o.depthLevel().name(),o.displayOrder(),o.status().name())).toList();}
}
