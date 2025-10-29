package com.carlosmoreno.store.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carlosmoreno.store.inventory_service.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
