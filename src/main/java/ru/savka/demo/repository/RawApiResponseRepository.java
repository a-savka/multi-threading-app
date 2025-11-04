package ru.savka.demo.repository;

import jakarta.transaction.Transactional;
import ru.savka.demo.entity.RawApiResponse;
import ru.savka.demo.entity.RawApiResponse.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawApiResponseRepository extends JpaRepository<RawApiResponse, Long> {

    List<RawApiResponse> findByStatusOrderByReceivedAtAsc(Status status, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE RawApiResponse r SET r.status = :newStatus, r.lastError = :lastError, r.attempts = :attempts WHERE r.id = :id AND r.status = :expectedStatus")
    int updateStatusById(
            @Param("id") Long id,
            @Param("newStatus") Status newStatus,
            @Param("lastError") String lastError,
            @Param("attempts") int attempts,
            @Param("expectedStatus") Status expectedStatus
    );

    @Transactional
    @Modifying
    @Query("UPDATE RawApiResponse r SET r.status = :newStatus WHERE r.id IN :ids AND r.status = :expectedStatus")
    int updateStatusByIdIn(
            @Param("ids") List<Long> ids,
            @Param("newStatus") Status newStatus,
            @Param("expectedStatus") Status expectedStatus
    );
}
