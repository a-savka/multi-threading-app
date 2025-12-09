package ru.savka.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.savka.demo.dto.SlotDto;
import ru.savka.demo.service.SlotService;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {
    private final SlotService slotService;

    @GetMapping("/free")
    public List<SlotDto> getFreeSlots(@RequestParam String speciality) {
        return slotService.getFreeSlotsBySpeciality(speciality);
    }

    @PostMapping("/{slotId}/reserve")
    public void reserveSlot(@PathVariable Long slotId, @RequestBody Long patientId) {
        slotService.reserveSlot(slotId, patientId);
    }
}
