package ru.savka.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
    private String message;
    private boolean success;
    private Object data; // Generic field to hold any response data

    public ApiResponseDto(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
