package com.devops.releasetracker.repository;

import com.devops.releasetracker.entity.DeploymentTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentTaskRepository extends JpaRepository<DeploymentTask, Long> {
    Page<DeploymentTask> findByReleaseId(Long releaseId, Pageable pageable);
}
