package com.household.inventory.service;

import com.household.inventory.entity.ExpiryAlertLog;
import com.household.inventory.entity.InventoryItem;
import com.household.inventory.entity.User;
import com.household.inventory.repository.ExpiryAlertLogRepository;
import com.household.inventory.repository.InventoryItemRepository;
import com.household.inventory.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiryAlertService {

    private static final Logger log = LoggerFactory.getLogger(ExpiryAlertService.class);

    private final InventoryItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ExpiryAlertLogRepository logRepo;
    private final EmailService emailService;

    private static final int NEAR_EXPIRY_DAYS = 3;

    // ✅ For testing, set to Duration.ZERO to always send
    private static final Duration RESEND_AFTER = Duration.ofHours(24);

    public ExpiryAlertService(InventoryItemRepository itemRepo,
                              UserRepository userRepo,
                              ExpiryAlertLogRepository logRepo,
                              EmailService emailService) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        log.info("✅ ExpiryAlertService loaded successfully");
    }

    // ✅ Run once immediately after app is ready (so you don't wait)
    @EventListener(ApplicationReadyEvent.class)
    public void runOnceOnStartup() {
        log.info("🚀 Running expiry check once on startup...");
        sendExpiryAlerts();
    }

    // ✅ Run every 1 minute for testing
    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10 * 1000)
    public void scheduledExpiryCheck() {
        sendExpiryAlerts();
    }

    public void sendExpiryAlerts() {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        log.info("⏰ Expiry job running at {}", now);

        List<InventoryItem> items = itemRepo.findAll();
        List<User> users = userRepo.findAll();

        log.info("Items found: {}, Users found: {}", items.size(), users.size());

        if (items.isEmpty() || users.isEmpty()) return;

        for (InventoryItem item : items) {

            if (item.getExpiryDate() == null) {
                log.info("Skipping (no expiryDate): {}", item.getName());
                continue;
            }

            boolean expired = item.getExpiryDate().isBefore(today);
            boolean nearExpiry = !expired && !item.getExpiryDate().isAfter(today.plusDays(NEAR_EXPIRY_DAYS));

            if (!expired && !nearExpiry) {
                log.info("Not expired/near-expiry: {} expiry={}", item.getName(), item.getExpiryDate());
                continue;
            }

            ExpiryAlertLog logRow = logRepo.findByItemId(item.getId()).orElse(null);

            boolean shouldSend = (logRow == null) ||
                    Duration.between(logRow.getLastSentAt(), now).compareTo(RESEND_AFTER) >= 0;

            if (!shouldSend) {
                log.info("Blocked by log (already sent recently): {}", item.getName());
                continue;
            }

            String status = expired ? "EXPIRED" : "NEAR EXPIRY";
            log.info("📧 Sending expiry email for: {} status={}", item.getName(), status);

            for (User user : users) {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    emailService.sendExpiryAlert(
                            user.getEmail(),
                            user.getUsername(),
                            item.getName(),
                            status,
                            item.getExpiryDate().toString()
                    );
                }
            }

            if (logRow == null) {
                logRepo.save(new ExpiryAlertLog(item.getId(), now));
            } else {
                logRow.setLastSentAt(now);
                logRepo.save(logRow);
            }
        }
    }
}
