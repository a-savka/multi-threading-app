package ru.savka.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.savka.demo.entity.Doctor;
import ru.savka.demo.entity.Patient;
import ru.savka.demo.entity.Slot;
import ru.savka.demo.repository.DoctorRepository;
import ru.savka.demo.repository.PatientRepository;
import ru.savka.demo.repository.SlotRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SlotRepository slotRepository;

    @Override
    public void run(String... args) throws Exception {
        // Создание врачей
        Doctor doctor1 = new Doctor();
        doctor1.setFullName("Иванов Иван Иванович");
        doctor1.setSpeciality("Терапевт");
        doctor1.setRoomNumber("101");

        Doctor doctor2 = new Doctor();
        doctor2.setFullName("Петров Петр Петрович");
        doctor2.setSpeciality("Хирург");
        doctor2.setRoomNumber("102");

        Doctor doctor3 = new Doctor();
        doctor3.setFullName("Сидоров Сидор Сидорович");
        doctor3.setSpeciality("Окулист");
        doctor3.setRoomNumber("103");

        Doctor doctor4 = new Doctor();
        doctor4.setFullName("Алексеев Алексей Алексеевич");
        doctor4.setSpeciality("Терапевт");
        doctor4.setRoomNumber("104");

        doctorRepository.saveAll(List.of(doctor1, doctor2, doctor3, doctor4));
        System.out.println("Добавлено 4 доктора");

        // Создание пациентов
        Patient patient1 = new Patient();
        patient1.setFullName("Сергеев Сергей Сергеевич");
        patient1.setPolicyNumber("111-111-111");
        patient1.setDateOfBirth(LocalDate.of(1990, 5, 15));

        Patient patient2 = new Patient();
        patient2.setFullName("Андреев Андрей Андреевич");
        patient2.setPolicyNumber("222-222-222");
        patient2.setDateOfBirth(LocalDate.of(1985, 10, 20));

        Patient patient3 = new Patient();
        patient3.setFullName("Дмитриев Дмитрий Дмитриевич");
        patient3.setPolicyNumber("333-333-333");
        patient3.setDateOfBirth(LocalDate.of(1995, 2, 25));

        patientRepository.saveAll(List.of(patient1, patient2, patient3));
        System.out.println("Добавлено 3 пациента");

        // Создание слотов
        List<Doctor> doctors = doctorRepository.findAll();
        LocalDate today = LocalDate.now();
        List<LocalTime> times = List.of(
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0)
        );

        for (Doctor doctor : doctors) {
            for (int i = 0; i < 3; i++) {
                LocalDate date = today.plusDays(i);
                for (LocalTime time : times) {
                    Slot slot = new Slot();
                    slot.setDoctor(doctor);
                    slot.setDate(date);
                    slot.setTime(time);
                    slot.setStatus("FREE");
                    slotRepository.save(slot);
                }
            }
        }
        System.out.println("Слоты на ближайшие 3 дня созданы");
    }
}
