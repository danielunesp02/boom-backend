package com.boom.student.snapshot.api;

import com.boom.student.snapshot.api.dto.SnapshotRebuildResponse;
import com.boom.student.snapshot.api.dto.StudentSkillDailySnapshotResponse;
import com.boom.student.snapshot.application.StudentSkillDailySnapshotService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StudentSkillDailySnapshotController {

    private final StudentSkillDailySnapshotService service;

    public StudentSkillDailySnapshotController(StudentSkillDailySnapshotService service) {
        this.service = service;
    }

    @PostMapping("/dev/snapshots/student-skills/daily/rebuild")
    public SnapshotRebuildResponse rebuildDailySnapshots(
            @RequestParam(value = "date", required = false) String date
    ) {
        return service.rebuildDailySnapshots(date);
    }

    @GetMapping("/students/{studentId}/snapshots/skills/daily")
    public List<StudentSkillDailySnapshotResponse> getStudentSkillSnapshots(
            @PathVariable("studentId") String studentId,
            @RequestParam(value = "date", required = false) String date,
            Authentication authentication
    ) {
        return service.getStudentSkillSnapshots(studentId, date, authentication);
    }
}
