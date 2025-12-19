package ru.savka.demo.domain.port.out;

import ru.savka.demo.domain.model.Slot;

import java.util.List;
import java.util.Optional;

public interface SlotRepositoryPort {
    List<Slot> findByDoctorSpecialityAndStatus(String speciality, String status);
    Optional<Slot> findById(Long id);
    Slot save(Slot slot);
}
