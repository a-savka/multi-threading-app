package ru.savka.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savka.demo.dto.DoctorDto;
import ru.savka.demo.entity.Doctor;
import ru.savka.demo.mapper.DoctorMapper;
import ru.savka.demo.repository.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public List<String> getSpecialities() {
        return doctorRepository.findDistinctSpecialities();
    }

    public List<DoctorDto> getDoctorsBySpeciality(String speciality) {
        return doctorRepository.findBySpeciality(speciality).stream()
                .map(doctorMapper::toDto)
                .collect(Collectors.toList());
    }
}
