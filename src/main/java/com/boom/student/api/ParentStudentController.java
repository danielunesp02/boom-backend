package com.boom.student.api;

import com.boom.student.api.dto.ParentStudentResponse;
import com.boom.student.application.AuthenticatedGuardianResolver;
import com.boom.student.application.ParentStudentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parents/students")
public class ParentStudentController {

    private final AuthenticatedGuardianResolver guardianResolver;
    private final ParentStudentService parentStudentService;

    public ParentStudentController(AuthenticatedGuardianResolver guardianResolver, ParentStudentService parentStudentService) {
        this.guardianResolver = guardianResolver;
        this.parentStudentService = parentStudentService;
    }

    @GetMapping
    public List<ParentStudentResponse> listStudents(Authentication authentication) {

        UUID guardianId = guardianResolver.requireGuardianId(authentication);

        return parentStudentService.listStudentsForGuardian(guardianId);
    }
}
