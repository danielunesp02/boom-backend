package com.boom.student.application;

import com.boom.student.domain.Student;
import com.boom.student.repository.GuardianStudentRelationshipRepository;
import com.boom.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class StudentAccessService {

    private final StudentRepository studentRepository;
    private final GuardianStudentRelationshipRepository relationshipRepository;

    public StudentAccessService(StudentRepository studentRepository, GuardianStudentRelationshipRepository relationshipRepository) {
        this.studentRepository = studentRepository;
        this.relationshipRepository = relationshipRepository;
    }

    public boolean canAccessStudent(UUID guardianId, UUID studentId) {
        return relationshipRepository.existsActiveRelationship(guardianId, studentId);
    }

    public void assertCanAccessStudent(UUID guardianId, UUID studentId) {
        if (!canAccessStudent(guardianId, studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Guardian cannot access this student.");
        }
    }

    public Student getPrimaryStudentForGuardian(UUID guardianId) {
        var relationship = relationshipRepository.findPrimaryActiveRelationship(guardianId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active primary student linked to this guardian."));
        return studentRepository.findById(relationship.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Primary student was not found."));
    }
}
