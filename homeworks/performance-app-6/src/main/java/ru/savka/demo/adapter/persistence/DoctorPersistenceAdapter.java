package ru.savka.demo.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.mapper.DoctorMapper;
import ru.savka.demo.adapter.persistence.repository.DoctorRepository;
import ru.savka.demo.domain.model.Doctor;
import ru.savka.demo.domain.port.out.DoctorRepositoryPort;


import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorPersistenceAdapter implements DoctorRepositoryPort {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;


    @Override
    public List<Doctor> saveAll(List<Doctor> doctors) {
        List<ru.savka.demo.adapter.persistence.entity.Doctor> doctorEntities = doctors.stream()
                .map(doctorMapper::toEntity)
                .collect(Collectors.toList());
        return doctorRepository.saveAll(doctorEntities).stream()
                .map(doctorMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDomain)
                .collect(Collectors.toList());
    }
}
