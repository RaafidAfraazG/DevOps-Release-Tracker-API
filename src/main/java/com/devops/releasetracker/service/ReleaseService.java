package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.ReleaseRejectionRequest;
import com.devops.releasetracker.dto.ReleaseRequest;
import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.dto.ReleaseStatusUpdateRequest;
import com.devops.releasetracker.entity.AuditLog;
import com.devops.releasetracker.entity.ApprovalStatus;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.exception.BadRequestException;
import com.devops.releasetracker.exception.ResourceNotFoundException;
import com.devops.releasetracker.mapper.ReleaseMapper;
import com.devops.releasetracker.repository.AuditLogRepository;
import com.devops.releasetracker.repository.ReleaseRepository;
import com.devops.releasetracker.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final AuditLogRepository auditLogRepository;
    private final ProjectService projectService;

    @Transactional
    public ReleaseResponse create(Long projectId, ReleaseRequest request) {
        Project project = projectService.findEntity(projectId);
        Release release = Release.builder()
                .project(project)
                .version(request.getVersion())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .plannedDate(request.getPlannedDate())
                .deployedDate(request.getDeployedDate())
                .build();
        release.setRiskScore(calculateRiskScore(release));
        return ReleaseMapper.toResponse(releaseRepository.save(release));
    }

    @Transactional(readOnly = true)
    public ReleaseResponse getById(Long id) {
        return ReleaseMapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReleaseResponse> list(Long projectId, ReleaseStatus status, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        return PageResponse.from(releaseRepository.findByFilters(projectId, status, fromDate, toDate, pageable)
                .map(ReleaseMapper::toResponse));
    }

    @Transactional
    public ReleaseResponse updateStatus(Long id, ReleaseStatusUpdateRequest request) {
        Release release = findEntity(id);
        ReleaseStatus oldStatus = release.getStatus();
        if (request.getStatus() == ReleaseStatus.DEPLOYED && release.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Release must be approved before deployment");
        }
        release.setStatus(request.getStatus());
        if (request.getStatus() == ReleaseStatus.DEPLOYED) {
            release.setDeployedDate(request.getDeployedDate() != null ? request.getDeployedDate() : LocalDate.now());
        } else if (request.getDeployedDate() != null) {
            release.setDeployedDate(request.getDeployedDate());
        }
        release.setRiskScore(calculateRiskScore(release));

        Release saved = releaseRepository.save(release);
        if (oldStatus != request.getStatus()) {
            auditLogRepository.save(AuditLog.builder()
                    .release(saved)
                    .oldStatus(oldStatus)
                    .newStatus(request.getStatus())
                    .changedBy(SecurityUtils.currentUsername())
                    .build());
        }
        return ReleaseMapper.toResponse(saved);
    }

    @Transactional
    public ReleaseResponse approve(Long id) {
        Release release = findEntity(id);
        release.setApprovalStatus(ApprovalStatus.APPROVED);
        release.setApprovedBy(SecurityUtils.currentUsername());
        release.setApprovedAt(Instant.now());
        release.setRejectionReason(null);
        return ReleaseMapper.toResponse(releaseRepository.save(release));
    }

    @Transactional
    public ReleaseResponse reject(Long id, ReleaseRejectionRequest request) {
        Release release = findEntity(id);
        release.setApprovalStatus(ApprovalStatus.REJECTED);
        release.setApprovedBy(null);
        release.setApprovedAt(null);
        release.setRejectionReason(request.getReason());
        return ReleaseMapper.toResponse(releaseRepository.save(release));
    }

    @Transactional
    public ReleaseResponse recalculateRiskScore(Long id) {
        Release release = findEntity(id);
        release.setRiskScore(calculateRiskScore(release));
        return ReleaseMapper.toResponse(releaseRepository.save(release));
    }

    int calculateRiskScore(Release release) {
        int incompleteTaskCount = (int) release.getTasks().stream()
                .filter(task -> !task.isCompleted())
                .count();
        int rollbackNoteCount = release.getRollbackNotes().size();

        int score = incompleteTaskCount * 10;
        score += rollbackNoteCount * 20;
        if (release.getStatus() == ReleaseStatus.FAILED || release.getStatus() == ReleaseStatus.ROLLED_BACK) {
            score += 30;
        }
        return Math.min(score, 100);
    }

    public Release findEntity(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Release not found with id " + id));
    }
}
