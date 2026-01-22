package com.household.inventory.service;

import com.household.inventory.entity.InventoryItem;
import com.household.inventory.entity.UsageHistory;
import com.household.inventory.repository.UsageHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockPredictionService {

    private final UsageHistoryRepository usageRepo;

    public StockPredictionService(UsageHistoryRepository usageRepo) {
        this.usageRepo = usageRepo;
    }

    public String predictRunOut(InventoryItem item) {

        List<UsageHistory> history =
                usageRepo.findByItemName(item.getName());

        if (history.isEmpty()) {
            return "No usage data";
        }

        int totalUsed = history.stream()
                .mapToInt(UsageHistory::getUsedQuantity)
                .sum();

        int days = history.size();
        double dailyAvg = (double) totalUsed / days;

        if (dailyAvg == 0) {
            return "Usage too low to predict";
        }

        int daysLeft = (int) Math.ceil(item.getQuantity() / dailyAvg);

        return "Estimated stock lasts ~ " + daysLeft + " days";
    }
}
