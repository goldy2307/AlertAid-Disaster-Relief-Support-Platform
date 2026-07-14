package com.alertaid.service;

import com.alertaid.model.Campaign;
import com.alertaid.model.Donation;
import com.alertaid.repository.CampaignRepository;
import com.alertaid.repository.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final SseService sseService;

    public DonationService(DonationRepository donationRepository, CampaignRepository campaignRepository, SseService sseService) {
        this.donationRepository = donationRepository;
        this.campaignRepository = campaignRepository;
        this.sseService = sseService;
    }

    public Donation donate(Long campaignId, Donation donation) {
        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign != null) {
            donation.setCampaign(campaign);
            campaign.setCollectedAmount(campaign.getCollectedAmount() + donation.getAmount());
            campaignRepository.save(campaign);
            Donation saved = donationRepository.save(donation);
            // Broadcast real-time update
            try {
                sseService.broadcast("donations.all", "donation", saved);
            } catch (Exception ignored) {}
            return saved;
        }
        return null;
    }

    public List<Donation> getDonations() {
        return donationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Donation> getDonationsByEmail(String email) {
        return donationRepository.findByDonorEmail(email);
    }
}
