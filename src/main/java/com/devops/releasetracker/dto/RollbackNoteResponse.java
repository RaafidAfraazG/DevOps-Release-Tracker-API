package com.devops.releasetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RollbackNoteResponse {
    private Long id;
    private Long releaseId;
    private String note;
    private String createdBy;
    private Instant createdAt;
}
