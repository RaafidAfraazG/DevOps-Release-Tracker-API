package com.devops.releasetracker.dto;

import com.devops.releasetracker.entity.ReleaseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private Long releaseId;
    private ReleaseStatus oldStatus;
    private ReleaseStatus newStatus;
    private String changedBy;
    private Instant changedAt;
}
