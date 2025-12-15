package ru.savka.demo.domain.port.out;

import ru.savka.demo.domain.model.Doctor;

import java.util.List;

public interface DoctorRepositoryPort {
    List<Doctor> saveAll(List<Doctor> doctors);
    List<Doctor> findAll();
}
