package ru.savka.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savka.demo.dto.DoctorDto;
import ru.savka.demo.service.DoctorService;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/specialities")
    public List<String> getSpecialities() {
        return doctorService.getSpecialities();
    }

    @GetMapping("/{speciality}")
    public List<DoctorDto> getDoctorsBySpeciality(@PathVariable String speciality) {
        return doctorService.getDoctorsBySpeciality(speciality);
    }
}
