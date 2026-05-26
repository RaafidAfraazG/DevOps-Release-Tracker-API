package com.devops.releasetracker.service;

import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.ProjectRequest;
import com.devops.releasetracker.dto.ProjectResponse;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.exception.BadRequestException;
import com.devops.releasetracker.exception.ResourceNotFoundException;
import com.devops.releasetracker.mapper.ProjectMapper;
import com.devops.releasetracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new BadRequestException("Project name already exists");
        }
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .repositoryUrl(request.getRepositoryUrl())
                .build();
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> list(Pageable pageable) {
        return PageResponse.from(projectRepository.findAll(pageable).map(ProjectMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        return ProjectMapper.toResponse(findEntity(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findEntity(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        return ProjectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        Project project = findEntity(id);
        projectRepository.delete(project);
    }

    public Project findEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
    }
}
