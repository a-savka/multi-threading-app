package ru.savka.demo.adapter.grpc;

import org.springframework.stereotype.Component;
import ru.savka.demo.domain.model.Doctor;


@Component
public class GrpcDoctorMapper {
    public ru.savka.demo.grpc.Doctor toGrpc(Doctor domain) {
        if (domain == null) {
            return ru.savka.demo.grpc.Doctor.newBuilder().build();
        }
        return ru.savka.demo.grpc.Doctor.newBuilder()
                .setId(domain.getId())
                .setFullName(domain.getFullName())
                .setSpeciality(domain.getSpeciality())
                .setRoomNumber(domain.getRoomNumber())
                .build();
    }
}
