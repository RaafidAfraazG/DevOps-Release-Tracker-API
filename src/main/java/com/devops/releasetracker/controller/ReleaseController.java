package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.ApiResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.ReleaseRejectionRequest;
import com.devops.releasetracker.dto.ReleaseRequest;
import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.dto.ReleaseStatusUpdateRequest;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.service.ReleaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReleaseController {

    private final ReleaseService releaseService;

    @PostMapping("/projects/{projectId}/releases")
    public ResponseEntity<ApiResponse<ReleaseResponse>> create(
            @PathVariable Long projectId,
            @Valid @RequestBody ReleaseRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Release created", releaseService.create(projectId, request)));
    }

    @GetMapping("/releases")
    public ResponseEntity<ApiResponse<PageResponse<ReleaseResponse>>> list(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) ReleaseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Releases fetched",
                releaseService.list(projectId, status, fromDate, toDate, pageable)
        ));
    }

    @GetMapping("/releases/{id}")
    public ResponseEntity<ApiResponse<ReleaseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Release fetched", releaseService.getById(id)));
    }

    @PatchMapping("/releases/{id}/status")
    public ResponseEntity<ApiResponse<ReleaseResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReleaseStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Release status updated", releaseService.updateStatus(id, request)));
    }

    @PatchMapping("/releases/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReleaseResponse>> approve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Release approved", releaseService.approve(id)));
    }

    @PatchMapping("/releases/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReleaseResponse>> reject(
            @PathVariable Long id,
            @Valid @RequestBody ReleaseRejectionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Release rejected", releaseService.reject(id, request)));
    }

    @PatchMapping("/releases/{id}/risk-score")
    public ResponseEntity<ApiResponse<ReleaseResponse>> recalculateRiskScore(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Release risk score recalculated", releaseService.recalculateRiskScore(id)));
    }
}
