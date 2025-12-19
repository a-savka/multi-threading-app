package ru.savka.demo.domain.model;

import java.time.LocalDate;

public class Patient {
    private Long id;
    private String policyNumber;
    private String fullName;
    private LocalDate dateOfBirth;

    public Patient() {
    }

    public Patient(Long id, String policyNumber, String fullName, LocalDate dateOfBirth) {
        this.id = id;
        this.policyNumber = policyNumber;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
