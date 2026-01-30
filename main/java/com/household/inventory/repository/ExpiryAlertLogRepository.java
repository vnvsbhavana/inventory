package com.household.inventory.repository;

import com.household.inventory.entity.ExpiryAlertLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpiryAlertLogRepository extends JpaRepository<ExpiryAlertLog, Long> {
    Optional<ExpiryAlertLog> findByItemId(Long itemId);
}
