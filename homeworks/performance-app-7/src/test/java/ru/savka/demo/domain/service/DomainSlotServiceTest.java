package ru.savka.demo.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.savka.demo.domain.model.Patient;
import ru.savka.demo.domain.model.Slot;
import ru.savka.demo.domain.port.in.SlotUseCase;
import ru.savka.demo.domain.port.out.PatientRepositoryPort;
import ru.savka.demo.domain.port.out.SlotRepositoryPort;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DomainSlotServiceTest {

    @Mock
    private SlotRepositoryPort slotRepositoryPort;

    @Mock
    private PatientRepositoryPort patientRepositoryPort;

    private SlotUseCase slotUseCase;

    @BeforeEach
    void setUp() {
        slotUseCase = new DomainSlotService(slotRepositoryPort, patientRepositoryPort);
    }

    @Test
    void reserveSlot_whenSlotIsFree_thenReserve() {
        long slotId = 1L;
        long patientId = 1L;
        Slot freeSlot = new Slot();
        freeSlot.setId(slotId);
        freeSlot.setStatus("FREE");
        Patient patient = new Patient();
        patient.setId(patientId);

        when(slotRepositoryPort.findById(slotId)).thenReturn(Optional.of(freeSlot));
        when(patientRepositoryPort.findById(patientId)).thenReturn(Optional.of(patient));

        slotUseCase.reserveSlot(slotId, patientId);

        verify(slotRepositoryPort).save(any(Slot.class));
    }

    @Test
    void reserveSlot_whenSlotIsTaken_thenThrowException() {
        long slotId = 1L;
        long patientId = 1L;
        Slot takenSlot = new Slot();
        takenSlot.setId(slotId);
        takenSlot.setStatus("RESERVED");

        when(slotRepositoryPort.findById(slotId)).thenReturn(Optional.of(takenSlot));

        assertThrows(RuntimeException.class, () -> slotUseCase.reserveSlot(slotId, patientId));
    }
}
