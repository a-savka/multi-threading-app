package ru.savka.demo.repository;

import ru.savka.demo.entity.ApiSourceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiSourceConfigRepository extends JpaRepository<ApiSourceConfig, String> {
}
