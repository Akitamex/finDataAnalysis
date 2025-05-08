package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateItemHistoryInput;
import com.ubm.ubmweb.graphql.dto.UpdateItemHistoryInput;
import com.ubm.ubmweb.model.ItemHistory;
import com.ubm.ubmweb.model.Item;
import com.ubm.ubmweb.service.ItemHistoryService;
import com.ubm.ubmweb.service.ItemService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item-history")
@RequiredArgsConstructor
public class ItemHistoryController {

    private final ItemHistoryService itemHistoryService;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemHistory> createItemHistory(@RequestParam UUID userId,
                                                          @RequestBody CreateItemHistoryInput input) {
        ItemHistory createdHistory = itemHistoryService.createItemHistory(userId, input);
        return ResponseEntity.ok(createdHistory);
    }

    @PutMapping
    public ResponseEntity<ItemHistory> updateItemHistory(@RequestParam UUID userId,
                                                          @RequestBody UpdateItemHistoryInput input) {
        ItemHistory updatedHistory = itemHistoryService.updateItemHistory(userId, input);
        return ResponseEntity.ok(updatedHistory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemHistory(@RequestParam UUID userId, 
                                                  @RequestParam UUID companyId,
                                                  @PathVariable UUID id) {
        itemHistoryService.deleteById(userId, companyId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemHistory> getItemHistoryById(@RequestParam UUID userId, 
                                                          @RequestParam UUID companyId, 
                                                          @PathVariable UUID id) {
        ItemHistory itemHistory = itemHistoryService.getItemHistoryById(userId, id, companyId);
        return ResponseEntity.ok(itemHistory);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemHistory>> getItemHistories(@RequestParam UUID userId, 
                                                              @RequestParam UUID companyId, 
                                                              @PathVariable UUID itemId) {
        Item item = itemService.getItemById(userId, itemId, companyId);
        List<ItemHistory> histories = itemHistoryService.getItemHistoryByItem(item);
        return ResponseEntity.ok(histories);
    }
}
