package com.household.inventory.controller;

import com.household.inventory.repository.UsageHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsageHistoryController {

    private final UsageHistoryRepository usageHistoryRepository;

    public UsageHistoryController(UsageHistoryRepository usageHistoryRepository) {
        this.usageHistoryRepository = usageHistoryRepository;
    }

    // SHOW USAGE HISTORY PAGE
    @GetMapping("/usage-history")
    public String viewUsageHistory(Model model) {
        model.addAttribute("historyList", usageHistoryRepository.findAll());
        return "usage-history";
    }
}
