package com.carlosmoreno.store.inventory_service.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.carlosmoreno.store.inventory_service.client.ProductClient;
import com.carlosmoreno.store.inventory_service.exception.InsufficientStockException;
import com.carlosmoreno.store.inventory_service.model.Inventory;
import com.carlosmoreno.store.inventory_service.model.PurchaseHistory;
import com.carlosmoreno.store.inventory_service.model.PurchaseRequest;
import com.carlosmoreno.store.inventory_service.repository.InventoryRepository;
import com.carlosmoreno.store.inventory_service.repository.PurchaseHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;
    private final PurchaseHistoryRepository historyRepository;

     @Transactional
    public synchronized Inventory purchase(PurchaseRequest request) {

        // Validar producto
        try {
            productClient.getProductById(request.getProductId());
        } catch (RuntimeException ex) {
            saveHistory(request, "FAILED", "Producto no existe");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El producto con ID " + request.getProductId() + " no existe");
        }

        // Buscar inventario
        Inventory inv = inventoryRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    saveHistory(request, "FAILED", "No stock para el producto");
                    return new InsufficientStockException("No stock for product " + request.getProductId());
                });

        // Validar cantidad
        if (inv.getQuantity() < request.getQuantity()) {
            saveHistory(request, "FAILED", "Stock insuficiente");
            throw new InsufficientStockException("Insufficient stock for product " + request.getProductId());
        }

        // Actualizar cantidad
        inv.setQuantity(inv.getQuantity() - request.getQuantity());
        Inventory saved = inventoryRepository.save(inv);

        // Registrar éxito
        saveHistory(request, "SUCCESS", "Compra realizada correctamente");

        return saved;
    }

    private void saveHistory(PurchaseRequest request, String status, String message) {
        PurchaseHistory history = PurchaseHistory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .date(LocalDateTime.now())
                .status(status)
                .message(message)
                .build();
        historyRepository.save(history);
    }
}
