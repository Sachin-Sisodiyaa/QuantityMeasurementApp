package com.app.measurementservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuantityInputDTO {

    @Valid
    @NotNull
    private QuantityDTO thisQuantityDTO;

    @Valid
    @NotNull
    private QuantityDTO thatQuantityDTO;
}
