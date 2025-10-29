package com.carlosmoreno.store.products_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.carlosmoreno.store.products_service.model.Product;
import com.carlosmoreno.store.products_service.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }
}