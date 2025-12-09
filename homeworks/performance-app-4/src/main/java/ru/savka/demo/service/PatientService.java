package ru.savka.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savka.demo.entity.Patient;
import ru.savka.demo.repository.PatientRepository;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public Long getPatientIdByPolicy(String policy) {
        return patientRepository.findByPolicyNumber(policy)
                .map(Patient::getId)
                .orElseThrow(() -> new RuntimeException("Пациент с таким полисом не найден"));
    }
}
