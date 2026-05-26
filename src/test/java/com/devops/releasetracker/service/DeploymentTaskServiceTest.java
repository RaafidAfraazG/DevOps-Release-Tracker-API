package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.DeploymentTaskResponse;
import com.devops.releasetracker.entity.DeploymentTask;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.repository.DeploymentTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeploymentTaskServiceTest {

    @Mock
    private DeploymentTaskRepository taskRepository;
    @Mock
    private ReleaseService releaseService;

    @InjectMocks
    private DeploymentTaskService deploymentTaskService;

    @Test
    void markCompletedSetsCompletedTrue() {
        Release release = Release.builder()
                .id(4L)
                .project(Project.builder().id(1L).name("Ops").repositoryUrl("https://github.com/example/ops").build())
                .version("v1")
                .title("Ops release")
                .status(ReleaseStatus.IN_PROGRESS)
                .plannedDate(LocalDate.now())
                .build();
        DeploymentTask task = DeploymentTask.builder()
                .id(11L)
                .release(release)
                .title("Smoke test")
                .assignedTo("qa@example.com")
                .completed(false)
                .build();

        when(taskRepository.findById(11L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        DeploymentTaskResponse response = deploymentTaskService.markCompleted(11L);

        assertThat(response.isCompleted()).isTrue();
    }
}
