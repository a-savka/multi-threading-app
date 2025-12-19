package ru.savka.demo.adapter.web.dto;

import lombok.Data;

@Data
public class DoctorDto {
    private Long id;
    private String fullName;
    private String speciality;
    private String roomNumber;
}
