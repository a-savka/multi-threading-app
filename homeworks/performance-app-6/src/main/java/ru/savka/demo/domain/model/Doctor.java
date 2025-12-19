package ru.savka.demo.domain.model;

public class Doctor {
    private Long id;
    private String fullName;
    private String speciality;
    private String roomNumber;

    public Doctor() {
    }

    public Doctor(Long id, String fullName, String speciality, String roomNumber) {
        this.id = id;
        this.fullName = fullName;
        this.speciality = speciality;
        this.roomNumber = roomNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
