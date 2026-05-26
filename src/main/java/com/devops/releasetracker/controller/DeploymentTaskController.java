package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.ApiResponse;
import com.devops.releasetracker.dto.DeploymentTaskRequest;
import com.devops.releasetracker.dto.DeploymentTaskResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.service.DeploymentTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DeploymentTaskController {

    private final DeploymentTaskService deploymentTaskService;

    @PostMapping("/releases/{releaseId}/tasks")
    public ResponseEntity<ApiResponse<DeploymentTaskResponse>> addTask(
            @PathVariable Long releaseId,
            @Valid @RequestBody DeploymentTaskRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deployment task created", deploymentTaskService.addTask(releaseId, request)));
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<DeploymentTaskResponse>> markCompleted(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success("Deployment task completed", deploymentTaskService.markCompleted(taskId)));
    }

    @GetMapping("/releases/{releaseId}/tasks")
    public ResponseEntity<ApiResponse<PageResponse<DeploymentTaskResponse>>> listByRelease(
            @PathVariable Long releaseId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Deployment tasks fetched",
                deploymentTaskService.listByRelease(releaseId, pageable)
        ));
    }
}
