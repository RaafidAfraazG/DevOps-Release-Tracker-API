package com.devops.releasetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DeploymentTaskResponse {
    private Long id;
    private Long releaseId;
    private String title;
    private String description;
    private boolean completed;
    private String assignedTo;
    private Instant createdAt;
    private Instant updatedAt;
}
