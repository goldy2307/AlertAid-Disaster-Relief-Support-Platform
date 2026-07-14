package com.alertaid.service;

import com.alertaid.model.SeekForHelp;
import com.alertaid.repository.SeekForHelpRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeekForHelpService {

    private final SeekForHelpRepository seekForHelpRepository;

    public SeekForHelpService(SeekForHelpRepository seekForHelpRepository) {
        this.seekForHelpRepository = seekForHelpRepository;
    }

    public List<SeekForHelp> getAllRequests() {
        return seekForHelpRepository.findAll();
    }

    public Optional<SeekForHelp> getRequestById(Long id) {
        return seekForHelpRepository.findById(id);
    }

    public SeekForHelp saveRequest(SeekForHelp request) {
        return seekForHelpRepository.save(request);
    }

    public void deleteRequest(Long id) {
        seekForHelpRepository.deleteById(id);
    }

    public SeekForHelp updateRequest(Long id, SeekForHelp updatedRequest) {
        return seekForHelpRepository.findById(id)
                .map(request -> {
                    request.setName(updatedRequest.getName());
                    request.setEmail(updatedRequest.getEmail());
                    request.setPhone(updatedRequest.getPhone());
                    request.setHelpType(updatedRequest.getHelpType());
                    request.setDescription(updatedRequest.getDescription());
                    return seekForHelpRepository.save(request);
                })
                .orElse(null);
    }
}