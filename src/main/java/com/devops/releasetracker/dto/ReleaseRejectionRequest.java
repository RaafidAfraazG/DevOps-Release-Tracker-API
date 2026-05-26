package com.devops.releasetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReleaseRejectionRequest {
    @NotBlank
    @Size(max = 1000)
    private String reason;
}
