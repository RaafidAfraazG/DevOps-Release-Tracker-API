package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.ReleaseRequest;
import com.devops.releasetracker.dto.ReleaseRejectionRequest;
import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.dto.ReleaseStatusUpdateRequest;
import com.devops.releasetracker.entity.AuditLog;
import com.devops.releasetracker.entity.ApprovalStatus;
import com.devops.releasetracker.entity.DeploymentTask;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.entity.RollbackNote;
import com.devops.releasetracker.exception.BadRequestException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        release.setApprovalStatus(ApprovalStatus.APPROVED);

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

    @Test
    void approveReleaseMarksApprovalMetadata() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("admin@example.com", null));
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.PLANNED);

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        ReleaseResponse response = releaseService.approve(5L);

        assertThat(response.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(response.getApprovedBy()).isEqualTo("admin@example.com");
        assertThat(response.getApprovedAt()).isNotNull();
        assertThat(response.getRejectionReason()).isNull();
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectReleaseStoresReasonAndClearsApprovalMetadata() {
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.PLANNED);
        release.setApprovalStatus(ApprovalStatus.APPROVED);
        release.setApprovedBy("admin@example.com");

        ReleaseRejectionRequest request = new ReleaseRejectionRequest();
        request.setReason("Smoke tests failed on staging");

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        ReleaseResponse response = releaseService.reject(5L, request);

        assertThat(response.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(response.getApprovedBy()).isNull();
        assertThat(response.getRejectionReason()).isEqualTo("Smoke tests failed on staging");
    }

    @Test
    void updateStatusRejectsDeploymentWhenReleaseIsNotApproved() {
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.IN_PROGRESS);

        ReleaseStatusUpdateRequest request = new ReleaseStatusUpdateRequest();
        request.setStatus(ReleaseStatus.DEPLOYED);

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));

        assertThatThrownBy(() -> releaseService.updateStatus(5L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Release must be approved before deployment");
    }

    @Test
    void recalculateRiskScoreAddsPointsForIncompleteTasksRollbackNotesAndFailedStatus() {
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.FAILED);
        release.getTasks().add(DeploymentTask.builder().title("Run smoke tests").assignedTo("qa@example.com").completed(false).build());
        release.getTasks().add(DeploymentTask.builder().title("Update release notes").assignedTo("dev@example.com").completed(true).build());
        release.getRollbackNotes().add(RollbackNote.builder().note("Rollback database migration").createdBy("lead@example.com").build());

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        ReleaseResponse response = releaseService.recalculateRiskScore(5L);

        assertThat(response.getRiskScore()).isEqualTo(60);
    }

    @Test
    void recalculateRiskScoreCapsScoreAtOneHundred() {
        Project project = sampleProject();
        Release release = sampleRelease(project, ReleaseStatus.ROLLED_BACK);
        release.getTasks().add(DeploymentTask.builder().title("Task 1").assignedTo("dev@example.com").completed(false).build());
        release.getTasks().add(DeploymentTask.builder().title("Task 2").assignedTo("dev@example.com").completed(false).build());
        release.getTasks().add(DeploymentTask.builder().title("Task 3").assignedTo("dev@example.com").completed(false).build());
        release.getTasks().add(DeploymentTask.builder().title("Task 4").assignedTo("dev@example.com").completed(false).build());
        release.getTasks().add(DeploymentTask.builder().title("Task 5").assignedTo("dev@example.com").completed(false).build());
        release.getRollbackNotes().add(RollbackNote.builder().note("Rollback note 1").createdBy("lead@example.com").build());
        release.getRollbackNotes().add(RollbackNote.builder().note("Rollback note 2").createdBy("lead@example.com").build());

        when(releaseRepository.findById(5L)).thenReturn(Optional.of(release));
        when(releaseRepository.save(release)).thenReturn(release);

        ReleaseResponse response = releaseService.recalculateRiskScore(5L);

        assertThat(response.getRiskScore()).isEqualTo(100);
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
                .approvalStatus(ApprovalStatus.PENDING)
                .plannedDate(LocalDate.of(2026, 6, 1))
                .build();
    }
}
