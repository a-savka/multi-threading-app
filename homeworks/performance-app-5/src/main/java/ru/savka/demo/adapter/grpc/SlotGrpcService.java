package ru.savka.demo.adapter.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.savka.demo.domain.port.in.SlotUseCase;
import ru.savka.demo.grpc.GetFreeSlotsRequest;
import ru.savka.demo.grpc.GetFreeSlotsResponse;
import ru.savka.demo.grpc.SlotServiceGrpc;

import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class SlotGrpcService extends SlotServiceGrpc.SlotServiceImplBase {

    private final SlotUseCase slotUseCase;
    private final GrpcSlotMapper grpcSlotMapper;

    @Override
    public void getFreeSlots(GetFreeSlotsRequest request, StreamObserver<GetFreeSlotsResponse> responseObserver) {
        GetFreeSlotsResponse response = GetFreeSlotsResponse.newBuilder()
                .addAllSlots(slotUseCase.getFreeSlotsBySpeciality(request.getSpeciality()).stream()
                        .map(grpcSlotMapper::toGrpc)
                        .collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
