package ru.savka.demo.adapter.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.web.dto.SlotDto;
import ru.savka.demo.domain.model.Slot;

@Component
@RequiredArgsConstructor
public class WebSlotMapper {

    private final WebDoctorMapper doctorMapper;

    public SlotDto toDto(Slot domain) {
        if (domain == null) {
            return null;
        }
        SlotDto dto = new SlotDto();
        dto.setId(domain.getId());
        dto.setDate(domain.getDate());
        dto.setTime(domain.getTime());
        dto.setDoctor(doctorMapper.toDto(domain.getDoctor()));
        return dto;
    }
}
