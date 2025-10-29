package com.carlosmoreno.store.products_service.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.carlosmoreno.store.products_service.model.Product;
import com.carlosmoreno.store.products_service.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct() {
        Product p = new Product();
        p.setName("Laptop");
        p.setPrice(1200.0);

        when(productRepository.save(any(Product.class))).thenReturn(p);

        Product saved = productService.save(p);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        assertEquals(1200.0, saved.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProductById_Found() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Mouse");

        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Mouse", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findById(99L);

        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void testFindAllProducts() {
        Product p1 = new Product();
        p1.setName("Keyboard");

        Product p2 = new Product();
        p2.setName("Monitor");

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> products = productService.findAll();

        assertEquals(2, products.size());
        assertEquals("Keyboard", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindAllProducts_Empty() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> products = productService.findAll();

        assertNotNull(products);
        assertTrue(products.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testSave_NullProduct_ThrowsException() {
        when(productRepository.save(null)).thenThrow(new IllegalArgumentException("Product cannot be null"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.save(null);
        });

        assertEquals("Product cannot be null", exception.getMessage());
        verify(productRepository, times(1)).save(null);
    }
}
