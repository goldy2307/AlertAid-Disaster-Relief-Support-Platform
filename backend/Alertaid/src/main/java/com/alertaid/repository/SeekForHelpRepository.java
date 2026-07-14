package com.alertaid.repository;

import com.alertaid.model.SeekForHelp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeekForHelpRepository extends JpaRepository<SeekForHelp, Long> {
}
