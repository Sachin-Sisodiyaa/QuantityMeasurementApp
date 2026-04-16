package com.app.measurementservice.unit;

public enum WeightUnit implements SupportsArithmetic {
    MG(0.000001),
    GRAM(0.001),
    KG(1.0),
    OUNCE(0.0283495),
    POUND(0.453592);

    private final double toKgConversionFactor;

    WeightUnit(double toKgConversionFactor) {
        this.toKgConversionFactor = toKgConversionFactor;
    }

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public String getMeasurementType() {
        return "Weight";
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * toKgConversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toKgConversionFactor;
    }

    public static WeightUnit fromName(String name) {
        try {
            return WeightUnit.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown weight unit: " + name);
        }
    }
}
