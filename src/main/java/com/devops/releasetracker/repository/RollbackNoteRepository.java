package com.devops.releasetracker.repository;

import com.devops.releasetracker.entity.RollbackNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RollbackNoteRepository extends JpaRepository<RollbackNote, Long> {
    Page<RollbackNote> findByReleaseId(Long releaseId, Pageable pageable);
}
