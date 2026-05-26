package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.RollbackNoteRequest;
import com.devops.releasetracker.dto.RollbackNoteResponse;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.entity.RollbackNote;
import com.devops.releasetracker.exception.BadRequestException;
import com.devops.releasetracker.repository.RollbackNoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RollbackNoteServiceTest {

    @Mock
    private RollbackNoteRepository rollbackNoteRepository;
    @Mock
    private ReleaseService releaseService;

    @InjectMocks
    private RollbackNoteService rollbackNoteService;

    @Test
    void addNoteAllowedForFailedRelease() {
        Release release = sampleRelease(ReleaseStatus.FAILED);
        RollbackNoteRequest request = new RollbackNoteRequest();
        request.setNote("Reverted migration and restored snapshot.");

        RollbackNote saved = RollbackNote.builder()
                .id(7L)
                .release(release)
                .note(request.getNote())
                .createdBy("system")
                .build();

        when(releaseService.findEntity(3L)).thenReturn(release);
        when(rollbackNoteRepository.save(any(RollbackNote.class))).thenReturn(saved);

        RollbackNoteResponse response = rollbackNoteService.addNote(3L, request);

        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getNote()).contains("Reverted");
    }

    @Test
    void addNoteRejectsPlannedRelease() {
        RollbackNoteRequest request = new RollbackNoteRequest();
        request.setNote("Cannot add this yet.");

        when(releaseService.findEntity(3L)).thenReturn(sampleRelease(ReleaseStatus.PLANNED));

        assertThatThrownBy(() -> rollbackNoteService.addNote(3L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Rollback notes can only be added to FAILED or ROLLED_BACK releases");
    }

    private Release sampleRelease(ReleaseStatus status) {
        Project project = Project.builder()
                .id(1L)
                .name("Core API")
                .repositoryUrl("https://github.com/example/core-api")
                .build();
        return Release.builder()
                .id(3L)
                .project(project)
                .version("v1")
                .title("Core release")
                .status(status)
                .plannedDate(LocalDate.now())
                .build();
    }
}
