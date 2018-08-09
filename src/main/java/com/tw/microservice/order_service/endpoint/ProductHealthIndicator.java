package com.tw.microservice.order_service.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        RestTemplate restTemplate = new RestTemplate();
        JsonNode productHealth = restTemplate.getForObject("http://localhost:8888/actuator/health", JsonNode.class);
        assert productHealth != null;
        String health = productHealth.get("status").asText();
        if (health.equals("Down")) {
            return Health.down().withDetail("Product health is not ok", productHealth).build();
        }
        return Health.up().build();

    }
}
