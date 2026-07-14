package com.alertaid.controller;

import com.alertaid.model.MyContribution;
import com.alertaid.service.MyContributionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contributions")
public class MyContributionController {

    private final MyContributionService contributionService;

    public MyContributionController(MyContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping
    public MyContribution createContribution(@RequestBody MyContribution contribution) {
        return contributionService.saveContribution(contribution);
    }

    @GetMapping
    public List<MyContribution> getAllContributions() {
        return contributionService.getAllContributions();
    }
}
