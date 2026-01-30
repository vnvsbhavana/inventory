package com.household.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "expiry_alert_log")
public class ExpiryAlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private LocalDateTime lastSentAt;

    public ExpiryAlertLog() {}

    public ExpiryAlertLog(Long itemId, LocalDateTime lastSentAt) {
        this.itemId = itemId;
        this.lastSentAt = lastSentAt;
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public LocalDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(LocalDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
