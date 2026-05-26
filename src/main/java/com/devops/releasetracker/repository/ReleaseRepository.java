package com.devops.releasetracker.repository;

import com.devops.releasetracker.entity.Release;
import com.devops.releasetracker.entity.ReleaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReleaseRepository extends JpaRepository<Release, Long> {

    @Query("""
            select r from Release r
            where (:projectId is null or r.project.id = :projectId)
              and (:status is null or r.status = :status)
              and (:fromDate is null or r.plannedDate >= :fromDate)
              and (:toDate is null or r.plannedDate <= :toDate)
            """)
    Page<Release> findByFilters(
            @Param("projectId") Long projectId,
            @Param("status") ReleaseStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
