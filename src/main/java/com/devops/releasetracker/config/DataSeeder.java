package com.devops.releasetracker.config;

import com.devops.releasetracker.entity.DeploymentTask;
import com.devops.releasetracker.entity.Project;
import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import com.devops.releasetracker.entity.Role;
import com.devops.releasetracker.entity.User;
import com.devops.releasetracker.repository.DeploymentTaskRepository;
import com.devops.releasetracker.repository.ProjectRepository;
import com.devops.releasetracker.repository.ReleaseRepository;
import com.devops.releasetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ReleaseRepository releaseRepository,
            DeploymentTaskRepository taskRepository
    ) {
        return args -> {
            if (!userRepository.existsByEmail("admin@releasetracker.dev")) {
                userRepository.save(User.builder()
                        .name("Admin User")
                        .email("admin@releasetracker.dev")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .build());
            }

            if (!userRepository.existsByEmail("developer@releasetracker.dev")) {
                userRepository.save(User.builder()
                        .name("Developer User")
                        .email("developer@releasetracker.dev")
                        .password(passwordEncoder.encode("Developer@123"))
                        .role(Role.DEVELOPER)
                        .build());
            }

            if (projectRepository.count() == 0) {
                Project project = projectRepository.save(Project.builder()
                        .name("CRM Deployment Automation")
                        .description("Sample project for tracking release readiness and deployment tasks.")
                        .repositoryUrl("https://github.com/example/crm-deployment-automation")
                        .build());

                Release release = releaseRepository.save(Release.builder()
                        .project(project)
                        .version("v1.0.0")
                        .title("Initial Production Release")
                        .description("Baseline CRM deployment with automated checks.")
                        .status(ReleaseStatus.IN_PROGRESS)
                        .plannedDate(LocalDate.now().plusDays(7))
                        .build());

                taskRepository.save(DeploymentTask.builder()
                        .release(release)
                        .title("Verify database migrations")
                        .description("Confirm migration scripts pass on staging before production deployment.")
                        .assignedTo("developer@releasetracker.dev")
                        .completed(false)
                        .build());
            }
        };
    }
}
