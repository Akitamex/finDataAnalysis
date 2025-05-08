package com.ubm.ubmweb.rest;

import com.ubm.ubmweb.graphql.dto.CreateItemInput;
import com.ubm.ubmweb.graphql.dto.UpdateItemInput;
import com.ubm.ubmweb.model.Item;
import com.ubm.ubmweb.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable UUID id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody CreateItemInput input, @RequestParam UUID userId) {
        Item createdItem = itemService.createItem(input, userId);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping
    public ResponseEntity<Item> updateItem(@RequestBody UpdateItemInput input, @RequestParam UUID userId) {
        Item updatedItem = itemService.updateItem(input, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id, @RequestParam UUID userId, @RequestParam UUID companyId) {
        itemService.deleteItem(id, userId, companyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Item>> findItems(
            @RequestParam UUID userId,
            @RequestParam UUID companyId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) String description) {
        List<Item> items = itemService.findItems(userId, companyId, name, types != null ? types : List.of(), description);
        return ResponseEntity.ok(items);
    }
}
