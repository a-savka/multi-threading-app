package ru.savka.demo.domain.port.out;

import ru.savka.demo.domain.model.Patient;

import java.util.Optional;

public interface PatientRepositoryPort {
    Optional<Patient> findById(Long id);
}
