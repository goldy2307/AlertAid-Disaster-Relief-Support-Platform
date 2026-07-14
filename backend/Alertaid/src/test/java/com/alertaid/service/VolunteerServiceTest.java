package com.alertaid.service;

import com.alertaid.model.Volunteer;
import com.alertaid.repository.VolunteerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class VolunteerServiceTest {

    @Test
    void saveAndListVolunteers() {
        VolunteerRepository repo = Mockito.mock(VolunteerRepository.class);
        VolunteerService service = new VolunteerService(repo);
        Volunteer v = new Volunteer();
        v.setFullName("Jane Doe");
        when(repo.save(Mockito.any(Volunteer.class))).thenReturn(v);
        when(repo.findAll()).thenReturn(List.of(v));

        Volunteer saved = service.saveVolunteer(v);
        assertThat(saved.getFullName()).isEqualTo("Jane Doe");
        assertThat(service.getAllVolunteers()).hasSize(1);
    }
}
