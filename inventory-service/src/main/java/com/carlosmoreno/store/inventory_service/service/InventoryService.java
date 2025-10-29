package com.carlosmoreno.store.inventory_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.carlosmoreno.store.inventory_service.client.ProductClient;
import com.carlosmoreno.store.inventory_service.model.Inventory;
import com.carlosmoreno.store.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;
    private final ProductClient productClient;

    public Inventory getInventory(Long productId) {
        try {
            productClient.getProductById(productId);
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto con ID " + productId + " no existe");
        }


        return repository.findById(productId)
            .orElseGet(() -> {
                Inventory newInv = Inventory.builder()
                        .productId(productId)
                        .quantity(0L)
                        .build();
                return repository.save(newInv);
            });
    }

    public Inventory updateQuantity(Long productId, Long quantity) {
        try {
            productClient.getProductById(productId);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "El producto con ID " + productId + " no existe"
            );
        }
        Inventory inv = repository.findById(productId).orElse(Inventory.builder()
                .productId(productId)
                .quantity(0L)
                .build());
        inv.setQuantity(quantity);
        return repository.save(inv);
    }

   
}
