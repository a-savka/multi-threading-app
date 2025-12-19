package ru.savka.demo.adapter.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.savka.demo.adapter.web.dto.SlotDto;
import ru.savka.demo.adapter.web.mapper.WebSlotMapper;
import ru.savka.demo.domain.port.in.SlotUseCase;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotUseCase slotUseCase;
    private final WebSlotMapper webSlotMapper;

    @GetMapping("/free")
    public List<SlotDto> getFreeSlots(@RequestParam String speciality) {
        return slotUseCase.getFreeSlotsBySpeciality(speciality).stream()
                .map(webSlotMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{slotId}/reserve")
    public void reserveSlot(@PathVariable Long slotId, @RequestBody Long patientId) {
        slotUseCase.reserveSlot(slotId, patientId);
    }
}
