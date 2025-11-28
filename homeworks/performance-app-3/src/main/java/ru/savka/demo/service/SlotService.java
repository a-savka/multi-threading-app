package ru.savka.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savka.demo.dto.SlotDto;
import ru.savka.demo.entity.Patient;
import ru.savka.demo.entity.Slot;
import ru.savka.demo.mapper.SlotMapper;
import ru.savka.demo.repository.PatientRepository;
import ru.savka.demo.repository.SlotRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {
    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;
    private final SlotMapper slotMapper;

    public List<SlotDto> getFreeSlotsBySpeciality(String speciality) {
        return slotRepository.findByDoctorSpecialityAndStatus(speciality, "FREE").stream()
                .map(slotMapper::toDto)
                .collect(Collectors.toList());
    }

    public void reserveSlot(Long slotId, Long patientId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Слот не найден"));
        if (!"FREE".equals(slot.getStatus())) {
            throw new RuntimeException("Слот уже занят");
        }
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Пациент не найден"));
        slot.setStatus("RESERVED");
        slot.setPatient(patient);
        slotRepository.save(slot);
    }
}
