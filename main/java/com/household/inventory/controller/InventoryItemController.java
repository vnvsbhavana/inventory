package com.household.inventory.controller;

import com.household.inventory.entity.InventoryItem;
import com.household.inventory.entity.UsageHistory;
import com.household.inventory.repository.InventoryItemRepository;
import com.household.inventory.repository.UsageHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory")
public class InventoryItemController {

    private final InventoryItemRepository inventoryRepository;
    private final UsageHistoryRepository usageHistoryRepository;

    public InventoryItemController(InventoryItemRepository inventoryRepository,
                                   UsageHistoryRepository usageHistoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.usageHistoryRepository = usageHistoryRepository;
    }

    // SHOW INVENTORY PAGE
    @GetMapping
    public String viewInventory(Model model) {
        model.addAttribute("items", inventoryRepository.findAll());
        return "inventory";
    }

    // ADD ITEM
    @PostMapping("/add")
    public String addItem(@ModelAttribute InventoryItem item) {
        inventoryRepository.save(item);
        return "redirect:/inventory";
    }

    // DELETE ITEM
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        inventoryRepository.deleteById(id);
        return "redirect:/inventory";
    }

    // SHOW EDIT PAGE
    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        InventoryItem item = inventoryRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/inventory";
        }
        model.addAttribute("item", item);
        return "edit-inventory";
    }

    // UPDATE ITEM
    @PostMapping("/update/{id}")
    public String updateItem(@PathVariable Long id,
                             @ModelAttribute InventoryItem updatedItem) {

        InventoryItem item = inventoryRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/inventory";
        }

        item.setName(updatedItem.getName());
        item.setQuantity(updatedItem.getQuantity());
        item.setCategory(updatedItem.getCategory());
        item.setPrice(updatedItem.getPrice());
        item.setExpiryDate(updatedItem.getExpiryDate());

        inventoryRepository.save(item);
        return "redirect:/inventory";
    }

    // USE ITEM + TRACK USAGE
    @PostMapping("/use/{id}")
    public String useItem(@PathVariable Long id,
                          @RequestParam int usedQuantity) {

        System.out.println("USED QTY = " + usedQuantity);

        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(item.getQuantity() - usedQuantity);
        inventoryRepository.save(item);

        UsageHistory history = new UsageHistory(item.getName(), usedQuantity);
        usageHistoryRepository.save(history);

        System.out.println("SAVED HISTORY FOR " + item.getName());

        return "redirect:/inventory";
    }

}
