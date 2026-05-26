package com.devops.releasetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;

    @Size(max = 1000)
    private String description;

    @NotBlank
    private String repositoryUrl;
}
