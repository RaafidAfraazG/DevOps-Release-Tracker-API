package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.AuditLogResponse;
import com.devops.releasetracker.entity.AuditLog;

public final class AuditLogMapper {
    private AuditLogMapper() {
    }

    public static AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .releaseId(auditLog.getRelease().getId())
                .oldStatus(auditLog.getOldStatus())
                .newStatus(auditLog.getNewStatus())
                .changedBy(auditLog.getChangedBy())
                .changedAt(auditLog.getChangedAt())
                .build();
    }
}
