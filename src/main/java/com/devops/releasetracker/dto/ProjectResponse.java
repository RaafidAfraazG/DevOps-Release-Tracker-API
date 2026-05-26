package com.devops.releasetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String repositoryUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
