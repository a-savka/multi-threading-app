package ru.savka.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;

    @Cacheable(value = "slots", key = "#speciality")
    public List<SlotDto> getFreeSlotsBySpeciality(String speciality) {
        // Имитация долгого запроса к базе данных
        try {
            Thread.sleep(1000); // 1 секунда
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        // Инвалидация кэша
        String speciality = slot.getDoctor().getSpeciality();
        cacheManager.getCache("slots").evict(speciality);
    }
}
