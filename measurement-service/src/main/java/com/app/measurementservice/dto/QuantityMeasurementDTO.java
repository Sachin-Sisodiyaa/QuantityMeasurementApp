package com.app.measurementservice.dto;

import com.app.measurementservice.model.QuantityMeasurementEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementDTO {
    private Long id;
    private Double thisValue;
    private String thisUnit;
    private String thisMeasurementType;
    private Double thatValue;
    private String thatUnit;
    private String thatMeasurementType;
    private String operation;
    private Double resultValue;
    private String resultUnit;
    private String resultMeasurementType;
    private String resultString;
    private Boolean isError;
    private String errorMessage;
    private String createdAt;
    private String updatedAt;

    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity entity) {
        return new QuantityMeasurementDTO(
                entity.getId(),
                entity.getThisValue(),
                entity.getThisUnit(),
                entity.getThisMeasurementType(),
                entity.getThatValue(),
                entity.getThatUnit(),
                entity.getThatMeasurementType(),
                entity.getOperation(),
                entity.getResultValue(),
                entity.getResultUnit(),
                entity.getResultMeasurementType(),
                entity.getResultString(),
                entity.isError(),
                entity.getErrorMessage(),
                entity.getCreatedAt() == null ? null : entity.getCreatedAt().toString(),
                entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().toString()
        );
    }

    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream().map(QuantityMeasurementDTO::fromEntity).collect(Collectors.toList());
    }
}
