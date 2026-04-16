package com.app.measurementservice.unit;

public enum TemperatureUnit implements IMeasurable {
    CELSIUS {
        @Override
        public double convertToBaseUnit(double value) {
            return value;
        }

        @Override
        public double convertFromBaseUnit(double value) {
            return value;
        }
    },
    FAHRENHEIT {
        @Override
        public double convertToBaseUnit(double value) {
            return (value - 32) * 5 / 9;
        }
        @Override
        public double convertFromBaseUnit(double value) {
            return value * 9 / 5 + 32;
        }
    },
    KELVIN {
        @Override
        public double convertToBaseUnit(double value) {
            return value - 273.15;
        }

        @Override
        public double convertFromBaseUnit(double value) {
            return value + 273.15;
        }
    };

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public String getMeasurementType() {
        return "Temperature";
    }

    @Override
    public void validateOperationSupport(String operation) {
        if ("ADD".equals(operation) || "SUBTRACT".equals(operation)) {
            throw new IllegalArgumentException("Temperature does not support arithmetic operations");
        }
    }

    public static TemperatureUnit fromName(String name) {
        try {
            return TemperatureUnit.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown temperature unit: " + name);
        }
    }
}
