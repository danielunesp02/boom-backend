package com.boom.student.player.api;

import com.boom.student.player.api.dto.StudentActivityPlayerResponse;
import com.boom.student.player.application.StudentActivityPlayerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students/{studentId}/activities/{activityId}/player")
public class StudentActivityPlayerController {

    private final StudentActivityPlayerService service;

    public StudentActivityPlayerController(StudentActivityPlayerService service) {
        this.service = service;
    }

    @GetMapping
    public StudentActivityPlayerResponse getPlayer(
            @PathVariable("studentId") String studentId,
            @PathVariable("activityId") String activityId,
            Authentication authentication
    ) {
        return service.getPlayer(studentId, activityId, authentication);
    }
}
