package com.carlosmoreno.store.inventory_service.services;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import com.carlosmoreno.store.inventory_service.client.ProductClient;
import com.carlosmoreno.store.inventory_service.model.Inventory;
import com.carlosmoreno.store.inventory_service.repository.InventoryRepository;
import com.carlosmoreno.store.inventory_service.service.InventoryService;

class InventoryServiceTest {

    @Mock
    private InventoryRepository repository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInventory_shouldReturnExistingInventory_whenFound() {
        // given
        Long productId = 1L;
        Inventory existing = Inventory.builder()
                .productId(productId)
                .quantity(5L)
                .build();

        when(productClient.getProductById(productId)).thenReturn(new HashMap<>());
        when(repository.findById(productId)).thenReturn(Optional.of(existing));

        // when
        Inventory result = inventoryService.getInventory(productId);

        // then
        assertNotNull(result);
        assertEquals(5L, result.getQuantity());
        verify(repository, never()).save(any());
    }

    @Test
    void getInventory_shouldCreateNewInventory_whenNotFound() {
        Long productId = 2L;
        when(productClient.getProductById(productId)).thenReturn(new HashMap<>());
        when(repository.findById(productId)).thenReturn(Optional.empty());

        Inventory newInv = Inventory.builder()
                .productId(productId)
                .quantity(0L)
                .build();
        when(repository.save(any(Inventory.class))).thenReturn(newInv);

        Inventory result = inventoryService.getInventory(productId);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(0L, result.getQuantity());
        verify(repository).save(any(Inventory.class));
    }

    @Test
    void getInventory_shouldThrowNotFound_whenProductClientFails() {
        Long productId = 3L;
        when(productClient.getProductById(productId)).thenThrow(new RuntimeException("No existe"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> inventoryService.getInventory(productId));

        assertEquals("404 NOT_FOUND \"El producto con ID 3 no existe\"", ex.getMessage());
        verify(repository, never()).findById(any());
    }

    // -------------------------------------------------------------------------
    // updateQuantity()
    // -------------------------------------------------------------------------
    @Test
    void updateQuantity_shouldUpdateExistingInventory() {
        Long productId = 10L;
        Long newQty = 8L;

        Inventory inv = Inventory.builder()
                .productId(productId)
                .quantity(3L)
                .build();

        when(productClient.getProductById(productId)).thenReturn(new HashMap<>());
        when(repository.findById(productId)).thenReturn(Optional.of(inv));
        when(repository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.updateQuantity(productId, newQty);

        assertEquals(newQty, result.getQuantity());
        verify(repository).save(inv);
    }

    @Test
    void updateQuantity_shouldCreateNewInventory_whenNotFound() {
        Long productId = 20L;
        Long newQty = 10L;

        when(productClient.getProductById(productId)).thenReturn(new HashMap<>());
        when(repository.findById(productId)).thenReturn(Optional.empty());
        when(repository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.updateQuantity(productId, newQty);

        assertEquals(productId, result.getProductId());
        assertEquals(newQty, result.getQuantity());
        verify(repository).save(any(Inventory.class));
    }

    @Test
    void updateQuantity_shouldThrowNotFound_whenProductClientFails() {
        Long productId = 30L;
        when(productClient.getProductById(productId)).thenThrow(new RuntimeException("no existe"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> inventoryService.updateQuantity(productId, 5L));

        assertTrue(ex.getMessage().contains("El producto con ID 30 no existe"));
        verify(repository, never()).save(any());
    }
}
