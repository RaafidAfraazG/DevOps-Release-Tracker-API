package com.devops.releasetracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "releases")
public class Release {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseStatus status;

    @Column(nullable = false)
    private LocalDate plannedDate;

    private LocalDate deployedDate;

    @Builder.Default
    @Column(nullable = false)
    private int riskScore = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private String approvedBy;
    private Instant approvedAt;

    @Column(length = 1000)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Builder.Default
    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeploymentTask> tasks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RollbackNote> rollbackNotes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditLog> auditLogs = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) {
            status = ReleaseStatus.PLANNED;
        }
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
