package ru.savka.service;

import org.springframework.stereotype.Service;
import ru.savka.model.Product;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private List<Product> products;

    public ProductService() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("products.csv")) {
            if (is == null) {
                throw new IllegalStateException("Cannot find products.csv in classpath");
            }
            try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                products = reader.lines()
                        .skip(1) // Skip header
                        .map(line -> {
                            String[] parts = line.split(",");
                            return new Product(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load products", e);
        }
    }

    public Optional<Product> getProductById(int id) {
        try {
            // Simulate network delay
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return products.stream()
                .filter(p -> p.id() == id)
                .findFirst();
    }
}
