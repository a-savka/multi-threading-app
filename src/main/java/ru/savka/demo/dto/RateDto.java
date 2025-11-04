package ru.savka.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private String currency;
    private BigDecimal rate;
    private LocalDateTime rateDate;
    private String sourceId;
}
