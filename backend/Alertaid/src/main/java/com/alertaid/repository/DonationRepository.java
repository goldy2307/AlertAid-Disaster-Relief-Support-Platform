package com.alertaid.repository;

import com.alertaid.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorEmail(String donorEmail);
    List<Donation> findAllByOrderByCreatedAtDesc();
}
