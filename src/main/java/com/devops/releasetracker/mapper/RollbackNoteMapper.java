package com.devops.releasetracker.mapper;

import com.devops.releasetracker.dto.RollbackNoteResponse;
import com.devops.releasetracker.entity.RollbackNote;

public final class RollbackNoteMapper {
    private RollbackNoteMapper() {
    }

    public static RollbackNoteResponse toResponse(RollbackNote note) {
        return RollbackNoteResponse.builder()
                .id(note.getId())
                .releaseId(note.getRelease().getId())
                .note(note.getNote())
                .createdBy(note.getCreatedBy())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
