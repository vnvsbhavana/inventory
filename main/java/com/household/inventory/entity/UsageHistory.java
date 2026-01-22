package com.household.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usage_history")
public class UsageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int usedQuantity;
    private LocalDate usedDate;

    public UsageHistory() {}

    public UsageHistory(String itemName, int usedQuantity) {
        this.itemName = itemName;
        this.usedQuantity = usedQuantity;
        this.usedDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(int usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }
// getters & setters
}
