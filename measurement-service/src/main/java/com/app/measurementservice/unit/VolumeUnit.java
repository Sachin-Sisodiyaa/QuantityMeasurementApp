package com.app.measurementservice.unit;

public enum VolumeUnit implements SupportsArithmetic {
    ML(0.001),
    LITER(1.0),
    GALLON(3.78541),
    PINT(0.473176),
    CUBIC_METER(1000.0);

    private final double toLiterConversionFactor;

    VolumeUnit(double toLiterConversionFactor) {
        this.toLiterConversionFactor = toLiterConversionFactor;
    }

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public String getMeasurementType() {
        return "Volume";
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * toLiterConversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toLiterConversionFactor;
    }

    public static VolumeUnit fromName(String name) {
        try {
            return VolumeUnit.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown volume unit: " + name);
        }
    }
}
