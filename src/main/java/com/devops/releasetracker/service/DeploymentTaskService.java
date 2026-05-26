package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.DeploymentTaskRequest;
import com.devops.releasetracker.dto.DeploymentTaskResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.entity.DeploymentTask;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.exception.ResourceNotFoundException;
import com.devops.releasetracker.mapper.DeploymentTaskMapper;
import com.devops.releasetracker.repository.DeploymentTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeploymentTaskService {

    private final DeploymentTaskRepository taskRepository;
    private final ReleaseService releaseService;

    @Transactional
    public DeploymentTaskResponse addTask(Long releaseId, DeploymentTaskRequest request) {
        Release release = releaseService.findEntity(releaseId);
        DeploymentTask task = DeploymentTask.builder()
                .release(release)
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(request.getAssignedTo())
                .completed(false)
                .build();
        return DeploymentTaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public DeploymentTaskResponse markCompleted(Long taskId) {
        DeploymentTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Deployment task not found with id " + taskId));
        task.setCompleted(true);
        return DeploymentTaskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public PageResponse<DeploymentTaskResponse> listByRelease(Long releaseId, Pageable pageable) {
        releaseService.findEntity(releaseId);
        return PageResponse.from(taskRepository.findByReleaseId(releaseId, pageable).map(DeploymentTaskMapper::toResponse));
    }
}
