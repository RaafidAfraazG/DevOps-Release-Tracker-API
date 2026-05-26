package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.ApiResponse;
import com.devops.releasetracker.dto.AuditLogResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/releases/{releaseId}/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> listByRelease(
            @PathVariable Long releaseId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched", auditLogService.listByRelease(releaseId, pageable)));
    }
}
