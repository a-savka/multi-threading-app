package ru.savka.demo.dto;

import lombok.Data;
import ru.savka.demo.entity.Doctor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SlotDto {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private Doctor doctor;
}
