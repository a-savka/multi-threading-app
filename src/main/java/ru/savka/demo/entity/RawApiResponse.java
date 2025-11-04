package ru.savka.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RawApiResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sourceId;
    private Instant receivedAt;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String payload;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int attempts;
    private String lastError;

    @Version
    private Long version;

    public enum Status {
        NEW, PROCESSING, PROCESSED, FAILED
    }
}
