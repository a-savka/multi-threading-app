package ru.savka.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ApiSourceConfig {

    @Id
    private String id;

    private String name;
    private String urlTemplate;
    private boolean enabled;
    private int timeoutMs;
    private Instant lastChecked;

    // Explicit setter for lastChecked, as Lombok might be having issues
    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }
}
