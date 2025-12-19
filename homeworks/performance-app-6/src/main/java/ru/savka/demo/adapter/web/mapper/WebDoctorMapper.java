package ru.savka.demo.adapter.web.mapper;

import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.web.dto.DoctorDto;
import ru.savka.demo.domain.model.Doctor;

@Component
public class WebDoctorMapper {
    public DoctorDto toDto(Doctor domain) {
        if (domain == null) {
            return null;
        }
        DoctorDto dto = new DoctorDto();
        dto.setId(domain.getId());
        dto.setFullName(domain.getFullName());
        dto.setSpeciality(domain.getSpeciality());
        dto.setRoomNumber(domain.getRoomNumber());
        return dto;
    }
}
