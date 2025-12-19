package ru.savka.demo.domain.service;

import ru.savka.demo.domain.model.Patient;
import ru.savka.demo.domain.model.Slot;
import ru.savka.demo.domain.port.in.SlotUseCase;
import ru.savka.demo.domain.port.out.PatientRepositoryPort;
import ru.savka.demo.domain.port.out.SlotRepositoryPort;

import java.util.List;

public class DomainSlotService implements SlotUseCase {

    private final SlotRepositoryPort slotRepositoryPort;
    private final PatientRepositoryPort patientRepositoryPort;

    public DomainSlotService(SlotRepositoryPort slotRepositoryPort, PatientRepositoryPort patientRepositoryPort) {
        this.slotRepositoryPort = slotRepositoryPort;
        this.patientRepositoryPort = patientRepositoryPort;
    }

    @Override
    public List<Slot> getFreeSlotsBySpeciality(String speciality) {
        return slotRepositoryPort.findByDoctorSpecialityAndStatus(speciality, "FREE");
    }

    @Override
    public void reserveSlot(Long slotId, Long patientId) {
        Slot slot = slotRepositoryPort.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Слот не найден"));
        if (!"FREE".equals(slot.getStatus())) {
            throw new RuntimeException("Слот уже занят");
        }
        Patient patient = patientRepositoryPort.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
        slot.setStatus("RESERVED");
        slot.setPatient(patient);
        slotRepositoryPort.save(slot);
    }
}
