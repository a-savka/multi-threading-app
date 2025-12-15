package ru.savka.demo.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.adapter.persistence.mapper.SlotMapper;
import ru.savka.demo.adapter.persistence.repository.SlotRepository;
import ru.savka.demo.domain.model.Slot;
import ru.savka.demo.domain.port.out.SlotRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SlotPersistenceAdapter implements SlotRepositoryPort {

    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;

    @Override
    public List<Slot> findByDoctorSpecialityAndStatus(String speciality, String status) {
        return slotRepository.findByDoctorSpecialityAndStatus(speciality, status).stream()
                .map(slotMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Slot> findById(Long id) {
        return slotRepository.findById(id).map(slotMapper::toDomain);
    }

    @Override
    public Slot save(Slot slot) {
        ru.savka.demo.adapter.persistence.entity.Slot slotEntity = slotMapper.toEntity(slot);
        return slotMapper.toDomain(slotRepository.save(slotEntity));
    }
}
