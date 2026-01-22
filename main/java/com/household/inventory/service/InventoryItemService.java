package com.household.inventory.service;

import com.household.inventory.entity.InventoryItem;
import com.household.inventory.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    // ✅ THIS METHOD FIXES YOUR ERROR
    public InventoryItem saveItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }



    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }

    public void deleteItem(Long id) {
        inventoryItemRepository.deleteById(id);
    }
    public InventoryItem getItemById(Long id) {
        return inventoryItemRepository.findById(id).orElse(null);
    }


}
