package com.carlosmoreno.store.inventory_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseRequest {
    private Long productId;
    private Long quantity;
}
