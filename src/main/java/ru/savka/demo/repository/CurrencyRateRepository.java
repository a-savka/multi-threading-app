package ru.savka.demo.repository;

import ru.savka.demo.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    Optional<CurrencyRate> findTopByCurrencyOrderByRateDateDesc(String currency);

    List<CurrencyRate> findByCurrencyAndRateDateBetween(String currency, LocalDateTime from, LocalDateTime to);
}
