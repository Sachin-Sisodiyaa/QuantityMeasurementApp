package com.app.measurementservice.unit;

public interface IMeasurable {

    String getUnitName();

    String getMeasurementType();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double value);

    default void validateOperationSupport(String operation) {
        // Default implementation allows all operations
    }

    static IMeasurable getUnitByName(String unitName, String measurementType) {
        if (unitName == null || measurementType == null) {
            throw new IllegalArgumentException("Unit name and measurement type are required");
        }

        String normalizedType = measurementType.trim().toLowerCase();
        String normalizedUnit = unitName.trim().toLowerCase();

        return switch (normalizedType) {
            case "length" -> LengthUnit.fromName(normalizedUnit);
            case "weight" -> WeightUnit.fromName(normalizedUnit);
            case "volume" -> VolumeUnit.fromName(normalizedUnit);
            case "temperature" -> TemperatureUnit.fromName(normalizedUnit);
            default -> throw new IllegalArgumentException("Unknown measurement type: " + measurementType);
        };
    }
}
