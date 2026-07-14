package com.alertaid.service;

import com.alertaid.model.SeekForHelp;
import com.alertaid.repository.SeekForHelpRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SeekForHelpServiceTest {

    @Test
    void saveAndListRequests() {
        SeekForHelpRepository repo = Mockito.mock(SeekForHelpRepository.class);
        SeekForHelpService service = new SeekForHelpService(repo);
        SeekForHelp req = new SeekForHelp();
        req.setName("Ravi");
        req.setHelpType("food");
        when(repo.save(Mockito.any(SeekForHelp.class))).thenReturn(req);
        when(repo.findAll()).thenReturn(List.of(req));

        SeekForHelp saved = service.saveRequest(req);
        assertThat(saved.getName()).isEqualTo("Ravi");
        assertThat(service.getAllRequests()).hasSize(1);
    }
}
