package com.boom.student.application;

import com.boom.student.api.dto.DevSeedStudentResponse;
import com.boom.student.api.dto.ParentStudentResponse;
import com.boom.student.domain.*;
import com.boom.student.repository.GuardianStudentRelationshipRepository;
import com.boom.student.repository.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ParentStudentService {

    private final StudentRepository studentRepository;
    private final GuardianStudentRelationshipRepository relationshipRepository;
    private final StudentAccessService studentAccessService;

    public ParentStudentService(StudentRepository studentRepository,
                                GuardianStudentRelationshipRepository relationshipRepository,
                                StudentAccessService studentAccessService) {
        this.studentRepository = studentRepository;
        this.relationshipRepository = relationshipRepository;
        this.studentAccessService = studentAccessService;
    }

    public List<ParentStudentResponse> listStudentsForGuardian(UUID guardianId) {
        return relationshipRepository.listStudentsForGuardian(guardianId);
    }

    public DevSeedStudentResponse seedHelenaForGuardian(UUID guardianId) {
        Instant now = Instant.now();
        Student student = new Student(UUID.randomUUID(), "Helena Bevilacqua", LocalDate.of(2013, 8, 10),
                GradeLevel.GRADE_7, TargetSchoolSystem.ITALY, "pt-BR", StudentStatus.ACTIVE, now, now);
        try {
            studentRepository.save(student);
            relationshipRepository.save(new GuardianStudentRelationship(UUID.randomUUID(), guardianId, student.id(),
                    RelationshipType.FATHER, true, GuardianStudentRelationshipStatus.ACTIVE, now, now));
            return new DevSeedStudentResponse(student.id(), student.displayName(), RelationshipType.FATHER.name(), true,
                    "Helena was created and linked to the authenticated guardian.");
        } catch (DataIntegrityViolationException duplicatePrimaryStudent) {
            Student primaryStudent = studentAccessService.getPrimaryStudentForGuardian(guardianId);
            return new DevSeedStudentResponse(primaryStudent.id(), primaryStudent.displayName(), RelationshipType.FATHER.name(), true,
                    "Authenticated guardian already has a primary active student.");
        }
    }
}
