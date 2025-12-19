package ru.savka.demo.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savka.demo.adapter.persistence.entity.Slot;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByDoctorSpecialityAndStatus(String speciality, String status);
}
