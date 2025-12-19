package ru.savka.demo.adapter.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.entity.Patient;


@Component
public class PatientMapper {
    public Patient toEntity(ru.savka.demo.domain.model.Patient domain) {
        if (domain == null) {
            return null;
        }
        Patient entity = new Patient();
        entity.setId(domain.getId());
        entity.setFullName(domain.getFullName());
        entity.setPolicyNumber(domain.getPolicyNumber());
        entity.setDateOfBirth(domain.getDateOfBirth());
        return entity;
    }

    public ru.savka.demo.domain.model.Patient toDomain(Patient entity) {
        if (entity == null) {
            return null;
        }
        ru.savka.demo.domain.model.Patient domain = new ru.savka.demo.domain.model.Patient();
        domain.setId(entity.getId());
        domain.setFullName(entity.getFullName());
        domain.setPolicyNumber(entity.getPolicyNumber());
        domain.setDateOfBirth(entity.getDateOfBirth());
        return domain;
    }
}
