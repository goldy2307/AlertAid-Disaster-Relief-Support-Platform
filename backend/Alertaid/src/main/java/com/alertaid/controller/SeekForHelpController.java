package com.alertaid.controller;

import com.alertaid.model.SeekForHelp;
import com.alertaid.service.SeekForHelpService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seekforhelp")
public class SeekForHelpController {

    private final SeekForHelpService seekForHelpService;

    public SeekForHelpController(SeekForHelpService seekForHelpService) {
        this.seekForHelpService = seekForHelpService;
    }

    @PostMapping
    public SeekForHelp createHelpRequest(@RequestBody SeekForHelp request) {
        return seekForHelpService.saveRequest(request);
    }

    @GetMapping
    public List<SeekForHelp> getAllRequests() {
        return seekForHelpService.getAllRequests();
    }
}
