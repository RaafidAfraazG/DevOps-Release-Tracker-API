package com.devops.releasetracker.dto;

import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class ReleaseResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String version;
    private String title;
    private String description;
    private ReleaseStatus status;
    private LocalDate plannedDate;
    private LocalDate deployedDate;
    private int riskScore;
    private ApprovalStatus approvalStatus;
    private String approvedBy;
    private Instant approvedAt;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
}
