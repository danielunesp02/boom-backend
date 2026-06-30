package com.boom.learning.api;
import com.boom.learning.api.dto.*; import com.boom.learning.application.CurriculumService; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/learning/curriculum") public class CurriculumController {
 private final CurriculumService service; public CurriculumController(CurriculumService service){this.service=service;}
 @GetMapping("/frameworks") public List<CurriculumFrameworkResponse> frameworks(){return service.listFrameworks().stream().map(f->new CurriculumFrameworkResponse(f.id().toString(),f.countryCode(),f.code(),f.name(),f.version(),f.sourceType().name(),f.status().name())).toList();}
 @GetMapping("/frameworks/{frameworkId}/bands") public List<CurriculumBandResponse> bands(@PathVariable UUID frameworkId){return service.listBands(frameworkId).stream().map(b->new CurriculumBandResponse(b.id().toString(),b.frameworkId().toString(),b.code(),b.minAgeMonths(),b.maxAgeMonths(),b.gradeLevel(),b.schoolStage(),b.displayOrder(),b.status().name())).toList();}
 @GetMapping("/bands/{bandId}/expectations") public List<CurriculumExpectationResponse> expectations(@PathVariable UUID bandId){return service.listExpectations(bandId).stream().map(e->new CurriculumExpectationResponse(e.id().toString(),e.bandId().toString(),e.subjectId().toString(),e.topicId().toString(),e.skillId().toString(),e.objectiveId()==null?null:e.objectiveId().toString(),e.expectedKnowledgeLevel().name(),e.expectedComplexityLevel().name(),e.expectedDepthLevel().name(),e.priority().name(),e.status().name())).toList();}
}
