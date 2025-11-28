package ru.savka.demo.mapper;

import org.springframework.stereotype.Component;
import ru.savka.demo.dto.DoctorDto;
import ru.savka.demo.entity.Doctor;

@Component
public class DoctorMapper {
    public DoctorDto toDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();
        dto.setId(doctor.getId());
        dto.setFullName(doctor.getFullName());
        dto.setSpeciality(doctor.getSpeciality());
        dto.setRoomNumber(doctor.getRoomNumber());
        return dto;
    }
}
