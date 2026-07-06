package com.boom.pedagogy.api;

import com.boom.pedagogy.api.dto.SubjectLearningStrategyResponse;
import com.boom.pedagogy.api.dto.UpdateSubjectLearningStrategyRequest;
import com.boom.pedagogy.application.SubjectLearningStrategyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students/{studentId}/subject-learning-strategies")
public class SubjectLearningStrategyController {

    private final SubjectLearningStrategyService service;

    public SubjectLearningStrategyController(SubjectLearningStrategyService service) {
        this.service = service;
    }

    @GetMapping
    public List<SubjectLearningStrategyResponse> list(@PathVariable("studentId") UUID studentId) {
        return service.listStrategies(studentId);
    }

    @GetMapping("/{subjectId}")
    public SubjectLearningStrategyResponse get(@PathVariable("studentId") UUID studentId, @PathVariable("subjectId") UUID subjectId) {
        return service.getStrategy(studentId, subjectId);
    }

    @PutMapping
    public SubjectLearningStrategyResponse update(
            @PathVariable("studentId") UUID studentId,
            @RequestBody UpdateSubjectLearningStrategyRequest request
    ) {
        return service.updateStrategy(studentId, request);
    }
}
