package com.boom.pedagogy.application;

public record PedagogicalReviewQueueMaintenanceResult(
        String date,
        int updatedQueueItems,
        int updatedMasteryStates,
        int dueItems,
        int overdueItems,
        int criticalOverdueItems
) {
}
