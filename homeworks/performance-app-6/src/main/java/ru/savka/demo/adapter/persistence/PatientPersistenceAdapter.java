package ru.savka.demo.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.mapper.PatientMapper;
import ru.savka.demo.adapter.persistence.repository.PatientRepository;
import ru.savka.demo.domain.model.Patient;
import ru.savka.demo.domain.port.out.PatientRepositoryPort;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientPersistenceAdapter implements PatientRepositoryPort {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id).map(patientMapper::toDomain);
    }
}
