package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.ReleaseRequest;
import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.dto.ReleaseStatusUpdateRequest;
import com.devops.releasetracker.entity.AuditLog;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.repository.AuditLogRepository;
import com.devops.releasetracker.repository.ReleaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ReleaseService releaseService;

    @Test
    void createReleaseUnderProject() {
        Project project = sampleProject();
        ReleaseRequest request = new ReleaseRequest();
        request.setVersion("v1.2.0");
        request.setTitle("Checkout release");
        request.setDescription("Payment fixes");
        request.setStatus(ReleaseStatus.PLANNED);
        request.setPlannedDate(LocalDate.of(2026, 6, 1));

        Release saved = sampleRelease(project, ReleaseStatus.PLANNED);
        saved.setVersion("v1.2.0");
        saved.setTitle("Checkout release");

        when(projectService.findEntity(1L)).thenReturn(project);
        when(releaseRepository.save(any(Release.class))).thenReturn(saved);

        ReleaseResponse response = releaseService.create(1L, request);

        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getVersion()).isEqualTo("v1.2.0");
    }

    @Test
    void updateStatusCreatesAuditLogWhenStatusChanges() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("lead@example.com", null));
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.IN_PROGRESS);

        ReleaseStatusUpdateRequest request = new ReleaseStatusUpdateRequest();
        request.setStatus(ReleaseStatus.DEPLOYED);

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        ReleaseResponse response = releaseService.updateStatus(5L, request);

        assertThat(response.getStatus()).isEqualTo(ReleaseStatus.DEPLOYED);
        assertThat(response.getDeployedDate()).isNotNull();
        verify(auditLogRepository).save(any(AuditLog.class));
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateStatusSkipsAuditLogWhenStatusIsUnchanged() {
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.PLANNED);

        ReleaseStatusUpdateRequest request = new ReleaseStatusUpdateRequest();
        request.setStatus(ReleaseStatus.PLANNED);

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        releaseService.updateStatus(5L, request);

        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    private Project sampleProject() {
        return Project.builder()
                .id(1L)
                .name("Payments")
                .repositoryUrl("https://github.com/example/payments")
                .build();
    }

    private Release sampleRelease(Project project, ReleaseStatus status) {
        return Release.builder()
                .id(5L)
                .project(project)
                .version("v1.0.0")
                .title("Release")
                .status(status)
                .plannedDate(LocalDate.of(2026, 6, 1))
                .build();
    }
}
