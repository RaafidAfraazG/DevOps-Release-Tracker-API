package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.DeploymentTaskResponse;
import com.devops.releasetracker.entity.DeploymentTask;

public final class DeploymentTaskMapper {
    private DeploymentTaskMapper() {
    }

    public static DeploymentTaskResponse toResponse(DeploymentTask task) {
        return DeploymentTaskResponse.builder()
                .id(task.getId())
                .releaseId(task.getRelease().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .assignedTo(task.getAssignedTo())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
