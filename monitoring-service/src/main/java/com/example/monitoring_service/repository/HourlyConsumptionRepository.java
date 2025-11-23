package com.example.monitoring_service.repository;

import com.example.monitoring_service.entity.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HourlyConsumptionRepository
        extends JpaRepository<HourlyConsumption, UUID> {

    Optional<HourlyConsumption> findByDeviceIdAndHourStart(UUID deviceId, Instant hourStart);

    List<HourlyConsumption> findAllByDeviceIdAndHourStartBetween(
            UUID deviceId, Instant startInclusive, Instant endExclusive);
}
