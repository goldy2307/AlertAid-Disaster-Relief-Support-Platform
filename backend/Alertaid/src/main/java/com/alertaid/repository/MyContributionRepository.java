package com.alertaid.repository;

import com.alertaid.model.MyContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyContributionRepository extends JpaRepository<MyContribution, Long> {
}
