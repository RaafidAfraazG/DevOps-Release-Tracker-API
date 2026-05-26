package com.devops.releasetracker.dto;

import com.devops.releasetracker.entity.ReleaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReleaseRequest {
    @NotBlank
    private String version;

    @NotBlank
    private String title;

    @Size(max = 2000)
    private String description;

    private ReleaseStatus status = ReleaseStatus.PLANNED;

    @NotNull
    private LocalDate plannedDate;

    private LocalDate deployedDate;
}
