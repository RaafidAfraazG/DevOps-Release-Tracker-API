package com.devops.releasetracker.controller;

import com.devops.releasetracker.dto.ReleaseRequest;
import com.devops.releasetracker.dto.ReleaseResponse;
import com.devops.releasetracker.dto.ReleaseStatusUpdateRequest;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.security.CustomUserDetailsService;
import com.devops.releasetracker.security.JwtService;
import com.devops.releasetracker.service.ReleaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReleaseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReleaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReleaseService releaseService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createReleaseUnderProjectReturnsCreated() throws Exception {
        ReleaseRequest request = new ReleaseRequest();
        request.setVersion("v2.0.0");
        request.setTitle("Billing release");
        request.setDescription("Billing workflow");
        request.setStatus(ReleaseStatus.PLANNED);
        request.setPlannedDate(LocalDate.of(2026, 7, 1));

        when(releaseService.create(eq(1L), any(ReleaseRequest.class))).thenReturn(ReleaseResponse.builder()
                .id(9L)
                .projectId(1L)
                .projectName("Billing")
                .version("v2.0.0")
                .title("Billing release")
                .status(ReleaseStatus.PLANNED)
                .plannedDate(LocalDate.of(2026, 7, 1))
                .build());

        mockMvc.perform(post("/api/projects/1/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.projectId").value(1))
                .andExpect(jsonPath("$.data.version").value("v2.0.0"));
    }

    @Test
    void updateReleaseStatusReturnsUpdatedStatus() throws Exception {
        ReleaseStatusUpdateRequest request = new ReleaseStatusUpdateRequest();
        request.setStatus(ReleaseStatus.DEPLOYED);
        request.setDeployedDate(LocalDate.of(2026, 7, 2));

        when(releaseService.updateStatus(eq(9L), any(ReleaseStatusUpdateRequest.class))).thenReturn(ReleaseResponse.builder()
                .id(9L)
                .projectId(1L)
                .projectName("Billing")
                .version("v2.0.0")
                .title("Billing release")
                .status(ReleaseStatus.DEPLOYED)
                .plannedDate(LocalDate.of(2026, 7, 1))
                .deployedDate(LocalDate.of(2026, 7, 2))
                .build());

        mockMvc.perform(patch("/api/releases/9/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DEPLOYED"))
                .andExpect(jsonPath("$.data.deployedDate").value("2026-07-02"));
    }
}
