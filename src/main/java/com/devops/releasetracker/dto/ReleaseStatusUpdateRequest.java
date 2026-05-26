package com.devops.releasetracker.dto;

import com.devops.releasetracker.entity.ReleaseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReleaseStatusUpdateRequest {
    @NotNull
    private ReleaseStatus status;

    private LocalDate deployedDate;
}
