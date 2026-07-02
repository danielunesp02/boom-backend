package com.boom.student.snapshot.api.dto;

public record SnapshotRebuildResponse(
        String snapshotDate,
        int rebuiltSnapshots
) {}
