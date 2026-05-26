package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.ApiResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.RollbackNoteRequest;
import com.devops.releasetracker.dto.RollbackNoteResponse;
import com.devops.releasetracker.service.RollbackNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/releases/{releaseId}/rollback-notes")
public class RollbackNoteController {

    private final RollbackNoteService rollbackNoteService;

    @PostMapping
    public ResponseEntity<ApiResponse<RollbackNoteResponse>> addNote(
            @PathVariable Long releaseId,
            @Valid @RequestBody RollbackNoteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rollback note created", rollbackNoteService.addNote(releaseId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RollbackNoteResponse>>> listByRelease(
            @PathVariable Long releaseId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success("Rollback notes fetched", rollbackNoteService.listByRelease(releaseId, pageable)));
    }
}
