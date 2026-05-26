package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.AuditLogResponse;
import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.mapper.AuditLogMapper;
import com.devops.releasetracker.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ReleaseService releaseService;

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> listByRelease(Long releaseId, Pageable pageable) {
        releaseService.findEntity(releaseId);
        return PageResponse.from(auditLogRepository.findByReleaseIdOrderByChangedAtDesc(releaseId, pageable)
                .map(AuditLogMapper::toResponse));
    }
}
