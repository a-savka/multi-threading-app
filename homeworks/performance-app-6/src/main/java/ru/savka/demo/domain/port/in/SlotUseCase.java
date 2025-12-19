package ru.savka.demo.domain.port.in;

import ru.savka.demo.domain.model.Slot;

import java.util.List;

public interface SlotUseCase {
    List<Slot> getFreeSlotsBySpeciality(String speciality);
    void reserveSlot(Long slotId, Long patientId);
}
