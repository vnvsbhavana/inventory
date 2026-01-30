package com.household.inventory.service;

import com.household.inventory.entity.InventoryItem;
import com.household.inventory.entity.LowStockAlertLog;
import com.household.inventory.entity.User;
import com.household.inventory.repository.InventoryItemRepository;
import com.household.inventory.repository.LowStockAlertLogRepository;
import com.household.inventory.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LowStockAlertService {

    private final InventoryItemRepository itemRepo;
    private final UserRepository userRepo;
    private final LowStockAlertLogRepository logRepo;
    private final EmailService emailService;

    // ✅ Don’t spam: send again only after this duration
    private static final Duration RESEND_AFTER = Duration.ofHours(24);

    public LowStockAlertService(InventoryItemRepository itemRepo,
                                UserRepository userRepo,
                                LowStockAlertLogRepository logRepo,
                                EmailService emailService) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.emailService = emailService;
    }

    // Runs every 1 hour (safe). Change as you like.
    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10 * 1000)

    public void sendLowStockAlerts() {

        List<InventoryItem> items = itemRepo.findAll();
        if (items.isEmpty()) return;

        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (InventoryItem item : items) {
            if (!item.isLowStock()) continue;

            // check last sent time for this item
            LowStockAlertLog log = logRepo.findByItemId(item.getId()).orElse(null);

            boolean shouldSend = (log == null) ||
                    Duration.between(log.getLastSentAt(), now).compareTo(RESEND_AFTER) >= 0;

            if (!shouldSend) continue;

            // send alert to all users (simple + works for your current setup)
            for (User user : users) {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    emailService.sendLowStockAlert(
                            user.getEmail(),
                            user.getUsername(),
                            item.getName(),
                            item.getQuantity(),
                            item.getMinQuantity()
                    );
                }
            }

            // update log
            if (log == null) {
                logRepo.save(new LowStockAlertLog(item.getId(), now));
            } else {
                log.setLastSentAt(now);
                logRepo.save(log);
            }
        }
    }
}
