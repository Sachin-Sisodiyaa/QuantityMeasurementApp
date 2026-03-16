package com.app.quantitymeasurement.service;

import java.util.List;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;

public interface IQuantityMeasurementService {

	boolean compare(QuantityDTO first, QuantityDTO second);

	QuantityDTO convert(QuantityDTO source, String targetUnit);

	QuantityDTO add(QuantityDTO first, QuantityDTO second);

	QuantityDTO add(QuantityDTO first, QuantityDTO second, String targetUnit);

	QuantityDTO subtract(QuantityDTO first, QuantityDTO second);

	QuantityDTO subtract(QuantityDTO first, QuantityDTO second, String targetUnit);

	double divide(QuantityDTO first, QuantityDTO second);

	List<QuantityMeasurementEntity> getMeasurementHistory();
}
