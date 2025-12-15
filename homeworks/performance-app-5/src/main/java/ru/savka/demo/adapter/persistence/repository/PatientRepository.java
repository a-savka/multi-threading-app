package ru.savka.demo.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savka.demo.adapter.persistence.entity.Patient;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPolicyNumber(String policyNumber);
}
