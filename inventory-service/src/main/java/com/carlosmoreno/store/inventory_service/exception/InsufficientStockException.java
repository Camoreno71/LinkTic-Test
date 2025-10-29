package com.carlosmoreno.store.inventory_service.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) { super(message); }
}
