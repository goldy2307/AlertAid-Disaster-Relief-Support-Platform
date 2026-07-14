package com.alertaid.service;

import com.alertaid.model.Volunteer;
import com.alertaid.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public List<Volunteer> getAllVolunteers() {
        return volunteerRepository.findAll();
    }

    public Optional<Volunteer> getVolunteerById(Long id) {
        return volunteerRepository.findById(id);
    }

    public Volunteer saveVolunteer(Volunteer volunteer) {
        return volunteerRepository.save(volunteer);
    }

    public void deleteVolunteer(Long id) {
        volunteerRepository.deleteById(id);
    }

    public Volunteer updateVolunteer(Long id, Volunteer updatedVolunteer) {
        return volunteerRepository.findById(id)
                .map(volunteer -> {
                    volunteer.setFullName(updatedVolunteer.getFullName());
                    volunteer.setEmail(updatedVolunteer.getEmail());
                    volunteer.setPhone(updatedVolunteer.getPhone());
                    volunteer.setAddress(updatedVolunteer.getAddress());
                    volunteer.setSkills(updatedVolunteer.getSkills());
                    volunteer.setAvailability(updatedVolunteer.getAvailability());
                    return volunteerRepository.save(volunteer);
                })
                .orElse(null);
    }
}