package ru.savka.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.savka.demo.domain.port.in.SlotUseCase;
import ru.savka.demo.domain.port.out.PatientRepositoryPort;
import ru.savka.demo.domain.port.out.SlotRepositoryPort;
import ru.savka.demo.domain.service.DomainSlotService;

@Configuration
public class BeanConfiguration {

    @Bean
    public SlotUseCase slotUseCase(SlotRepositoryPort slotRepositoryPort, PatientRepositoryPort patientRepositoryPort) {
        return new DomainSlotService(slotRepositoryPort, patientRepositoryPort);
    }
}
