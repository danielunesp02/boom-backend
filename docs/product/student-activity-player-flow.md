# STUDENT-001 — Student Activity Player Flow

## Goal

Create a beautiful, tablet/mobile-first activity answering flow for students.

## Experience principles

```text
one question per screen
large touch targets
clear progress
simple visual hierarchy
friendly AI coach bubble
instant feedback
low cognitive load
works well on tablet and mobile browsers
```

## Initial backend endpoint

```http
GET /api/v1/students/{studentId}/activities/{activityId}/player
```

## Response concept

```text
student
activity
aiCoach
questions[]
  options[]
  hint
```

## Mock AI strategy

The first version uses a mock AI coach.

This lets the product validate:

```text
visual experience
copy tone
interaction model
student engagement
feedback placement
tablet/mobile usability
```

Before paying for or depending on a real AI provider.

## Future AI service contract

```text
AiCoachService
  activityIntro(...)
  questionHint(...)
  answerFeedback(...)
  completionSummary(...)
```

## Frontend components planned

```text
StudentShell
ActivityPlayerPage
QuestionCard
AnswerOptionCard
AiCoachBubble
ProgressStepper
ActivityTimer
FeedbackPanel
ActivityResultCard
RecommendedNextActionCard
```

## UX target

The activity should feel closer to:

```text
Duolingo
Khan Academy Kids
modern SaaS dashboard
touch-first learning app
```

But not overly childish, because Boom may also support teenagers.
