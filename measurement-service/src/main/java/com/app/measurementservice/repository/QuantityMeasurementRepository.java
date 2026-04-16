package com.app.measurementservice.repository;

import com.app.measurementservice.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    List<QuantityMeasurementEntity> findByOperation(String operation);

    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    @Query("SELECT q FROM QuantityMeasurementEntity q WHERE q.operation = :operation AND q.isError = false")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);

    long countByOperationAndIsErrorFalse(String operation);

    List<QuantityMeasurementEntity> findByIsErrorTrue();

    long deleteByThisMeasurementType(String measurementType);
}
