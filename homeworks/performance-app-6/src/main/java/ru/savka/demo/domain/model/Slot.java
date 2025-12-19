package ru.savka.demo.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Slot {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private Doctor doctor;
    private Patient patient;

    public Slot() {
    }

    public Slot(Long id, LocalDate date, LocalTime time, String status, Doctor doctor, Patient patient) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
        this.doctor = doctor;
        this.patient = patient;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
