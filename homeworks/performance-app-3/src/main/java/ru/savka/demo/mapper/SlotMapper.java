package ru.savka.demo.mapper;

import org.springframework.stereotype.Component;
import ru.savka.demo.dto.SlotDto;
import ru.savka.demo.entity.Slot;

@Component
public class SlotMapper {
    public SlotDto toDto(Slot slot) {
        SlotDto dto = new SlotDto();
        dto.setId(slot.getId());
        dto.setDate(slot.getDate());
        dto.setTime(slot.getTime());
        dto.setDoctor(slot.getDoctor());
        return dto;
    }
}
