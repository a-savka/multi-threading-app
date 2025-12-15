package ru.savka.demo.adapter.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.savka.demo.domain.model.Slot;

@Component
@RequiredArgsConstructor
public class GrpcSlotMapper {

    private final GrpcDoctorMapper doctorMapper;

    public ru.savka.demo.grpc.Slot toGrpc(Slot domain) {
        if (domain == null) {
            return ru.savka.demo.grpc.Slot.newBuilder().build();
        }
        return ru.savka.demo.grpc.Slot.newBuilder()
                .setId(domain.getId())
                .setDate(domain.getDate().toString())
                .setTime(domain.getTime().toString())
                .setDoctor(doctorMapper.toGrpc(domain.getDoctor()))
                .build();
    }
}
