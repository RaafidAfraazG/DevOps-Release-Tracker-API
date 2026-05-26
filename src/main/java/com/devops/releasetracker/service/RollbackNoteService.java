package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.RollbackNoteRequest;
import com.devops.releasetracker.dto.RollbackNoteResponse;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.entity.RollbackNote;
import com.devops.releasetracker.exception.BadRequestException;
import com.devops.releasetracker.mapper.RollbackNoteMapper;
import com.devops.releasetracker.repository.RollbackNoteRepository;
import com.devops.releasetracker.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RollbackNoteService {

    private final RollbackNoteRepository rollbackNoteRepository;
    private final ReleaseService releaseService;

    @Transactional
    public RollbackNoteResponse addNote(Long releaseId, RollbackNoteRequest request) {
        Release release = releaseService.findEntity(releaseId);
        if (release.getStatus() != ReleaseStatus.FAILED && release.getStatus() != ReleaseStatus.ROLLED_BACK) {
            throw new BadRequestException("Rollback notes can only be added to FAILED or ROLLED_BACK releases");
        }
        RollbackNote note = RollbackNote.builder()
                .release(release)
                .note(request.getNote())
                .createdBy(SecurityUtils.currentUsername())
                .build();
        return RollbackNoteMapper.toResponse(rollbackNoteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public PageResponse<RollbackNoteResponse> listByRelease(Long releaseId, Pageable pageable) {
        releaseService.findEntity(releaseId);
        return PageResponse.from(rollbackNoteRepository.findByReleaseId(releaseId, pageable)
                .map(RollbackNoteMapper::toResponse));
    }
}
