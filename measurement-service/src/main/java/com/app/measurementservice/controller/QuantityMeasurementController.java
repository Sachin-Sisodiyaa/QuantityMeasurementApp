package com.app.measurementservice.controller;

import com.app.measurementservice.auth.AuthClient;
import com.app.measurementservice.dto.QuantityInputDTO;
import com.app.measurementservice.dto.QuantityMeasurementDTO;
import com.app.measurementservice.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;
    private final AuthClient authClient;

    public QuantityMeasurementController(IQuantityMeasurementService service, AuthClient authClient) {
        this.service = service;
        this.authClient = authClient;
    }

    private void validateUser() {
        authClient.me();
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /compare");
        validateUser();
        QuantityMeasurementDTO result = service.compareQuantities(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a target unit")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /convert");
        validateUser();
        QuantityMeasurementDTO result = service.convertQuantity(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /add");
        validateUser();
        QuantityMeasurementDTO result = service.addQuantities(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /subtract");
        validateUser();
        QuantityMeasurementDTO result = service.subtractQuantities(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/multiply")
    @Operation(summary = "Multiply two compatible quantities")
    public ResponseEntity<QuantityMeasurementDTO> multiplyQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /multiply");
        validateUser();
        QuantityMeasurementDTO result = service.multiplyQuantities(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(@Valid @RequestBody QuantityInputDTO input) {
        logger.info("POST /divide");
        validateUser();
        QuantityMeasurementDTO result = service.divideQuantities(
                input.getThisQuantityDTO(), input.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get measurement history by operation type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(@PathVariable String operation) {
        logger.info("GET /history/operation/{}", operation);
        validateUser();
        List<QuantityMeasurementDTO> history = service.getHistoryByOperation(operation);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get measurement history by measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getMeasurementTypeHistory(@PathVariable String measurementType) {
        logger.info("GET /history/type/{}", measurementType);
        validateUser();
        List<QuantityMeasurementDTO> history = service.getHistoryByMeasurementType(measurementType);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get count of successful operations by type")
    public ResponseEntity<Long> getOperationCount(@PathVariable String operation) {
        logger.info("GET /count/{}", operation);
        validateUser();
        long count = service.getCountByOperation(operation);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get error history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        logger.info("GET /history/errored");
        validateUser();
        List<QuantityMeasurementDTO> errors = service.getErrorHistory();
        return ResponseEntity.ok(errors);
    }

    @DeleteMapping("/history/type/{measurementType}")
    @Operation(summary = "Clear measurement history by measurement type")
    public ResponseEntity<Long> clearMeasurementTypeHistory(@PathVariable String measurementType) {
        logger.info("DELETE /history/type/{}", measurementType);
        validateUser();
        long deleted = service.clearHistoryByMeasurementType(measurementType);
        return ResponseEntity.ok(deleted);
    }

    @PostMapping("/history/type/{measurementType}/clear")
    @Operation(summary = "Clear measurement history by measurement type using POST fallback")
    public ResponseEntity<Long> clearMeasurementTypeHistoryPost(@PathVariable String measurementType) {
        logger.info("POST /history/type/{}/clear", measurementType);
        validateUser();
        long deleted = service.clearHistoryByMeasurementType(measurementType);
        return ResponseEntity.ok(deleted);
    }
}
