package com.devops.releasetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RollbackNoteRequest {
    @NotBlank
    @Size(max = 2000)
    private String note;
}
