package ru.savka.demo.adapter.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.entity.Doctor;


@Component
public class DoctorMapper {

    public Doctor toEntity(ru.savka.demo.domain.model.Doctor domain) {
        if (domain == null) {
            return null;
        }
        Doctor entity = new Doctor();
        entity.setId(domain.getId());
        entity.setFullName(domain.getFullName());
        entity.setSpeciality(domain.getSpeciality());
        entity.setRoomNumber(domain.getRoomNumber());
        return entity;
    }

    public ru.savka.demo.domain.model.Doctor toDomain(Doctor entity) {
        if (entity == null) {
            return null;
        }
        ru.savka.demo.domain.model.Doctor domain = new ru.savka.demo.domain.model.Doctor();
        domain.setId(entity.getId());
        domain.setFullName(entity.getFullName());
        domain.setSpeciality(entity.getSpeciality());
        domain.setRoomNumber(entity.getRoomNumber());
        return domain;
    }
}
