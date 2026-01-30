package com.household.inventory.controller;

import com.household.inventory.entity.InventoryItem;
import com.household.inventory.entity.UsageHistory;
import com.household.inventory.repository.InventoryItemRepository;
import com.household.inventory.repository.UsageHistoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

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

    // ✅ SHOW INVENTORY PAGE + TOTAL AMOUNT + LOGGED-IN USERNAME
    @GetMapping
    public String viewInventory(Model model, Principal principal) {

        List<InventoryItem> items = inventoryRepository.findAll();

        BigDecimal totalAmount = items.stream()
                .map(i -> BigDecimal.valueOf(i.getPrice())
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        String username = (principal != null) ? principal.getName() : "User";

        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("username", username);

        return "inventory";
    }

    // ✅ ADD ITEM
    @PostMapping("/add")
    public String addItem(@ModelAttribute InventoryItem item) {
        inventoryRepository.save(item);
        return "redirect:/inventory";
    }

    // ✅ DELETE ITEM
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        inventoryRepository.deleteById(id);
        return "redirect:/inventory";
    }

    // ✅ SHOW EDIT PAGE
    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        InventoryItem item = inventoryRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/inventory";
        }
        model.addAttribute("item", item);
        return "edit-inventory";
    }

    // ✅ UPDATE ITEM
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

    // ✅ USE ITEM + TRACK USAGE
    @PostMapping("/use/{id}")
    public String useItem(@PathVariable Long id,
                          @RequestParam int usedQuantity) {

        if (usedQuantity <= 0) {
            return "redirect:/inventory";
        }

        InventoryItem item = inventoryRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/inventory";
        }

        item.setQuantity(Math.max(item.getQuantity() - usedQuantity, 0));
        inventoryRepository.save(item);

        usageHistoryRepository.save(new UsageHistory(item.getName(), usedQuantity));

        return "redirect:/inventory";
    }
}
