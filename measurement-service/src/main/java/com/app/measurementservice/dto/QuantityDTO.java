package com.app.measurementservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuantityDTO {

    @NotNull
    private Double value;

    @NotBlank
    private String unit;

    @NotBlank
    private String measurementType;
}
