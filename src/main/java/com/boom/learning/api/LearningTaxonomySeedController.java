package com.boom.learning.api;
import com.boom.learning.api.dto.LearningTaxonomySeedResponse; import com.boom.learning.application.LearningTaxonomySeedService; import org.springframework.context.annotation.Profile; import org.springframework.web.bind.annotation.*;
@Profile("local") @RestController @RequestMapping("/api/v1/dev/seed/learning-taxonomy") public class LearningTaxonomySeedController {
 private final LearningTaxonomySeedService service; public LearningTaxonomySeedController(LearningTaxonomySeedService service){this.service=service;}
 @PostMapping public LearningTaxonomySeedResponse seed(){var r=service.seed(); return new LearningTaxonomySeedResponse(r.subjects(),r.topics(),r.skills(),r.objectives(),r.frameworks(),r.bands(),r.expectations(),r.references());}
}
