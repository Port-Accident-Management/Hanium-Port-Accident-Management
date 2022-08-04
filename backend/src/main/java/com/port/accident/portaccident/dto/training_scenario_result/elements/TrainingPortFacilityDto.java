package com.port.accident.portaccident.dto.training_scenario_result.elements;


import com.port.accident.portaccident.domain.staff.Staff;
import com.port.accident.portaccident.domain.training_scenario_result.TrainingResult;
import com.port.accident.portaccident.domain.training_scenario_result.elements.TrainingPortFacility;
import com.port.accident.portaccident.enums.PortFacility;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class TrainingPortFacilityDto {
    private PortFacility name;
    private TrainingResult trainingResult;

    @Builder
    public TrainingPortFacilityDto(PortFacility name, TrainingResult trainingResult) {
        this.name = name;
        this.trainingResult = trainingResult;
    }

    public TrainingPortFacility toEntity() {
        return TrainingPortFacility.builder()
                .name(name)
                .trainingResult(trainingResult)
                .build();
    }
}
