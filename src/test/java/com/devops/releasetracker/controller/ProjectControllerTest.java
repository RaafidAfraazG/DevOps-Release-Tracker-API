package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.PageResponse;
import com.devops.releasetracker.dto.ProjectRequest;
import com.devops.releasetracker.dto.ProjectResponse;
import com.devops.releasetracker.security.CustomUserDetailsService;
import com.devops.releasetracker.security.JwtService;
import com.devops.releasetracker.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void listProjectsReturnsPagedResponse() throws Exception {
        ProjectResponse project = ProjectResponse.builder()
                .id(1L)
                .name("Release Hub")
                .repositoryUrl("https://github.com/example/release-hub")
                .build();
        when(projectService.list(any(Pageable.class))).thenReturn(PageResponse.<ProjectResponse>builder()
                .content(List.of(project))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Release Hub"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void createProjectReturnsCreated() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setName("Release Hub");
        request.setDescription("Tracks releases");
        request.setRepositoryUrl("https://github.com/example/release-hub");

        when(projectService.create(any(ProjectRequest.class))).thenReturn(ProjectResponse.builder()
                .id(1L)
                .name("Release Hub")
                .description("Tracks releases")
                .repositoryUrl("https://github.com/example/release-hub")
                .build());

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Release Hub"));
    }
}
