package ru.savka.demo.adapter.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.entity.Slot;

@Component
@RequiredArgsConstructor
public class SlotMapper {
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;


    public Slot toEntity(ru.savka.demo.domain.model.Slot domain) {
        if (domain == null) {
            return null;
        }
        Slot entity = new Slot();
        entity.setId(domain.getId());
        entity.setDate(domain.getDate());
        entity.setTime(domain.getTime());
        entity.setStatus(domain.getStatus());
        entity.setDoctor(doctorMapper.toEntity(domain.getDoctor()));
        entity.setPatient(patientMapper.toEntity(domain.getPatient()));
        return entity;
    }

    public ru.savka.demo.domain.model.Slot toDomain(Slot entity) {
        if (entity == null) {
            return null;
        }
        ru.savka.demo.domain.model.Slot domain = new ru.savka.demo.domain.model.Slot();
        domain.setId(entity.getId());
        domain.setDate(entity.getDate());
        domain.setTime(entity.getTime());
        domain.setStatus(entity.getStatus());
        domain.setDoctor(doctorMapper.toDomain(entity.getDoctor()));
        domain.setPatient(patientMapper.toDomain(entity.getPatient()));
        return domain;
    }
}
