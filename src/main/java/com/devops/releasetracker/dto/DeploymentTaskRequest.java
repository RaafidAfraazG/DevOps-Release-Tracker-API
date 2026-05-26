package com.devops.releasetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeploymentTaskRequest {
    @NotBlank
    private String title;

    @Size(max = 1000)
    private String description;

    @NotBlank
    private String assignedTo;
}
