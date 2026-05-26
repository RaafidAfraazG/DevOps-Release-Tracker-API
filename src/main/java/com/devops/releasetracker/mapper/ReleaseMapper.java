package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.entity.Release;

public final class ReleaseMapper {
    private ReleaseMapper() {
    }

    public static ReleaseResponse toResponse(Release release) {
        return ReleaseResponse.builder()
                .id(release.getId())
                .projectId(release.getProject().getId())
                .projectName(release.getProject().getName())
                .version(release.getVersion())
                .title(release.getTitle())
                .description(release.getDescription())
                .status(release.getStatus())
                .plannedDate(release.getPlannedDate())
                .deployedDate(release.getDeployedDate())
                .riskScore(release.getRiskScore())
                .approvalStatus(release.getApprovalStatus())
                .approvedBy(release.getApprovedBy())
                .approvedAt(release.getApprovedAt())
                .rejectionReason(release.getRejectionReason())
                .createdAt(release.getCreatedAt())
                .updatedAt(release.getUpdatedAt())
                .build();
    }
}
