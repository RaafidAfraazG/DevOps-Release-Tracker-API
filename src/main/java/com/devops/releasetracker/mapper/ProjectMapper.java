package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.ProjectResponse;
import com.devops.releasetracker.entity.Project;

public final class ProjectMapper {
    private ProjectMapper() {
    }

    public static ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .repositoryUrl(project.getRepositoryUrl())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
