# EPIC — Teacher Profile & Classroom Reports

## Objetivo

Adicionar o perfil de professor ao Boom, permitindo acesso a relatórios de performance dos alunos, turmas, gaps, evolução, atividades e intervenções.

## Modelos de cadastro de aluno

O aluno pode ser cadastrado por:

```text
PARENT_CREATED
SCHOOL_CREATED
TEACHER_CREATED
BULK_IMPORT
INVITATION_ACCEPTED
```

## Entidades propostas

```text
TeacherAccount
School
Classroom
ClassroomEnrollment
TeacherStudentRelationship
SchoolStudentRelationship
StudentAccessGrant
StudentInvitation
StudentRegistrationSource
```

## Modelo de acesso

O professor só deve acessar alunos por:

```text
direct teacher-student relationship
classroom enrollment
school assignment
explicit access grant
```

## Capacidades do professor

Relatórios:

```text
class overview
student progress
subject performance
topic performance
skill mastery
learning gaps
activity completion
engagement trend
risk indicators
```

Ações:

```text
assign activity to student
assign activity to class
open action plan
comment on learning gap
recommend parent review
export classroom report
```

## Restrições

Professor não deve acessar:

```text
guardian private account data
billing data
unrelated students
sensitive family details
other schools/classes unless authorized
```

## Stories

```text
TEACH-001 Teacher Account Foundation
TEACH-002 School and Classroom Foundation
TEACH-003 Classroom Enrollment
TEACH-004 Teacher Reports Overview
TEACH-005 Student Performance Report for Teacher
TEACH-006 Activity Assignment by Teacher
TEACH-007 Student Invitation and Registration Source
```

## Access control matrix

| Actor | Can create student | Can view report | Can assign activity | Can see guardian data |
|---|---:|---:|---:|---:|
| Parent/Guardian | Yes | Own students | Optional | Own account only |
| Teacher | Yes, if allowed | Assigned/class students | Yes | No |
| School Admin | Yes | School students | Optional | Limited |
| Platform Admin | Yes | Yes | Yes | Controlled/internal only |
