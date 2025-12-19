package ru.savka.demo.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.savka.demo.adapter.persistence.entity.Doctor;


import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpeciality(String speciality);

    @Query("SELECT DISTINCT d.speciality FROM Doctor d")
    List<String> findDistinctSpecialities();
}
