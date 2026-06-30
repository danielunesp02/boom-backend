package com.boom.student.api;

import com.boom.student.api.dto.DevSeedStudentResponse;
import com.boom.student.application.AuthenticatedGuardianResolver;
import com.boom.student.application.ParentStudentService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Profile("local")
@RestController
@RequestMapping("/api/v1/dev/seed")
public class DevSeedController {

    private final AuthenticatedGuardianResolver guardianResolver;
    private final ParentStudentService parentStudentService;

    public DevSeedController(AuthenticatedGuardianResolver guardianResolver, ParentStudentService parentStudentService) {
        this.guardianResolver = guardianResolver;
        this.parentStudentService = parentStudentService;
    }

    @PostMapping("/helena")
    public DevSeedStudentResponse seedHelena(Authentication authentication) {
        UUID guardianId = guardianResolver.requireGuardianId(authentication);
        return parentStudentService.seedHelenaForGuardian(guardianId);
    }
}
