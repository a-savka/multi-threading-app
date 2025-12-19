package ru.savka.demo.adapter.web.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotDto {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private DoctorDto doctor;
}
