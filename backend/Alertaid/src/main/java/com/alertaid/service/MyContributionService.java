package com.alertaid.service;

import com.alertaid.model.MyContribution;
import com.alertaid.repository.MyContributionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyContributionService {

    private final MyContributionRepository contributionRepository;

    public MyContributionService(MyContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    public List<MyContribution> getAllContributions() {
        return contributionRepository.findAll();
    }

    public Optional<MyContribution> getContributionById(Long id) {
        return contributionRepository.findById(id);
    }

    public MyContribution saveContribution(MyContribution contribution) {
        return contributionRepository.save(contribution);
    }

    public void deleteContribution(Long id) {
        contributionRepository.deleteById(id);
    }

    public MyContribution updateContribution(Long id, MyContribution updatedContribution) {
        return contributionRepository.findById(id)
                .map(contribution -> {
                    contribution.setContributorName(updatedContribution.getContributorName());
                    contribution.setEmail(updatedContribution.getEmail());
                    contribution.setContributionType(updatedContribution.getContributionType());
                    contribution.setDetails(updatedContribution.getDetails());
                    contribution.setAmount(updatedContribution.getAmount());
                    return contributionRepository.save(contribution);
                })
                .orElse(null);
    }
}