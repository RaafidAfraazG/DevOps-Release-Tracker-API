package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.ProjectRequest;
import com.devops.releasetracker.dto.ProjectResponse;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.exception.ResourceNotFoundException;
import com.devops.releasetracker.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProjectPersistsAndMapsResponse() {
        ProjectRequest request = new ProjectRequest();
        request.setName("Release Hub");
        request.setDescription("Tracks deployments");
        request.setRepositoryUrl("https://github.com/example/release-hub");

        Project saved = Project.builder()
                .id(10L)
                .name("Release Hub")
                .description("Tracks deployments")
                .repositoryUrl("https://github.com/example/release-hub")
                .build();

        when(projectRepository.existsByName("Release Hub")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ProjectResponse response = projectService.create(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Release Hub");
    }

    @Test
    void getByIdThrowsWhenProjectDoesNotExist() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found with id 99");
    }
}
