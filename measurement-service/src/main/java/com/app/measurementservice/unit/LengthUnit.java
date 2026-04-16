package com.app.measurementservice.unit;

public enum LengthUnit implements SupportsArithmetic {
    MM(0.001),
    CM(0.01),
    METER(1.0),
    KM(1000.0),
    INCH(0.0254),
    FOOT(0.3048),
    YARD(0.9144),
    MILE(1609.34);

    private final double toMeterConversionFactor;

    LengthUnit(double toMeterConversionFactor) {
        this.toMeterConversionFactor = toMeterConversionFactor;
    }

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public String getMeasurementType() {
        return "Length";
    }

    @Override
    public double convertToBaseUnit(double value) {
        return value * toMeterConversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toMeterConversionFactor;
    }

    public static LengthUnit fromName(String name) {
        try {
            return LengthUnit.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown length unit: " + name);
        }
    }
}
