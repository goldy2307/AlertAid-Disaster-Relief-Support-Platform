package com.alertaid.service;

import com.alertaid.model.Campaign;
import com.alertaid.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CampaignServiceTest {

    @Test
    void createAndListCampaigns() {
        CampaignRepository repo = Mockito.mock(CampaignRepository.class);
        CampaignService service = new CampaignService(repo);
        Campaign c = new Campaign();
        c.setTitle("Flood Relief");
        when(repo.save(Mockito.any(Campaign.class))).thenReturn(c);
        when(repo.findAll()).thenReturn(List.of(c));

        Campaign saved = service.createCampaign(c);
        assertThat(saved.getTitle()).isEqualTo("Flood Relief");
        assertThat(service.getAllCampaigns()).hasSize(1);
    }
}
