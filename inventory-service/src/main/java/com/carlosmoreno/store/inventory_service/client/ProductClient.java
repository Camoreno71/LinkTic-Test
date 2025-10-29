package com.carlosmoreno.store.inventory_service.client;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class ProductClient {

    private final WebClient webClient;
    private final String apiKey;

    public ProductClient(@Value("${products.service.url:http://products-service:8080}") String baseUrl,
                         @Value("${api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public Map<String, Object> getProductById(Long productId) {
        try {
            return webClient.get()
                    .uri("/api/products/{id}", productId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            resp -> Mono.error(new RuntimeException("Product not found")))
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(2))
                    .retryWhen(
                        Retry.fixedDelay(2, Duration.ofMillis(500))
                            .filter(throwable -> !(throwable.getMessage().contains("Product not found")))
                    )
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException("Product not found");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to call products service: " + ex.getMessage(), ex);
        }
    }
}
