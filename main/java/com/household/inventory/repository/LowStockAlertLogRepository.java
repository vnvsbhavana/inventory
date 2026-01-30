package com.household.inventory.repository;

import com.household.inventory.entity.LowStockAlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LowStockAlertLogRepository extends JpaRepository<LowStockAlertLog, Long> {
    Optional<LowStockAlertLog> findByItemId(Long itemId);
}
