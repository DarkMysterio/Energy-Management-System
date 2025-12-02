package com.example.monitoring_service.controller;

import com.example.monitoring_service.dto.DailyConsumptionResponse;
import com.example.monitoring_service.dto.MultiDeviceRequest;
import com.example.monitoring_service.dto.UserTotalConsumptionResponse;
import com.example.monitoring_service.service.ConsumptionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @GetMapping("/consumption/{deviceId}")
    public ResponseEntity<DailyConsumptionResponse> getDailyConsumption(
            @PathVariable UUID deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {


        if (!consumptionService.deviceExists(deviceId)) {
            return ResponseEntity.notFound().build();
        }

        DailyConsumptionResponse response = consumptionService.getDailyConsumption(deviceId, date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/consumption/total")
    public ResponseEntity<UserTotalConsumptionResponse> getTotalConsumption(
            @RequestBody MultiDeviceRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (request.getDevices() == null || request.getDevices().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserTotalConsumptionResponse response = consumptionService.getTotalConsumptionForDevices(request.getDevices(), date);
        return ResponseEntity.ok(response);
    }

}
