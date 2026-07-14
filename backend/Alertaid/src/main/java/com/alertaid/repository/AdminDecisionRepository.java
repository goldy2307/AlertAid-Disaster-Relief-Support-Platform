package com.alertaid.repository;

import com.alertaid.model.AdminDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDecisionRepository extends JpaRepository<AdminDecision, Long> {
}