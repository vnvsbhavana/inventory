package com.household.inventory.repository;

import com.household.inventory.entity.UsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageHistoryRepository extends JpaRepository<UsageHistory, Long> {
    List<UsageHistory> findByItemName(String itemName);
}
