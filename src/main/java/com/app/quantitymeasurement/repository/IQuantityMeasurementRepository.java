
package com.apps.quantitymeasurementapp.repository;

import java.util.List;

import com.apps.quantitymeasurementapp.entity.QuantityMeasurementEntity;

public interface IQuantityMeasurementRepository {

	void saveMeasurement(QuantityMeasurementEntity entity);

	List<QuantityMeasurementEntity> getAllMeasurements();
}
