package com.carlosmoreno.store.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carlosmoreno.store.inventory_service.model.PurchaseHistory;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
}
