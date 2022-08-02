package com.port.accident.portaccident.domain.training_scenario;


import com.port.accident.portaccident.domain.training_scenario.scenario_evaluation.ScenarioEvaluation;
import com.port.accident.portaccident.domain.training_scenario.elements.AccidentPortFacility;
import com.port.accident.portaccident.domain.training_scenario.elements.AccidentResponseActivity;
import com.port.accident.portaccident.dto.training_scenario.ScenarioDto;
import com.port.accident.portaccident.dto.training_scenario.elements.AccidentPortFacilityDto;
import com.port.accident.portaccident.dto.training_scenario.elements.AccidentResponseActivityDto;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "scenario")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_id") // 훈련 시나리오 id
    private Integer id;

    @Column(name = "scenario_name") // 시나리오명
    private String name;

    @Column(name = "scenario_level") // 사고 수준
    private String level;

    @Column(name = "scenario_impact") // 사고 영향
    private String impact;

    /**
     * precedingType : 사고 - 재난 둘 중에 어떤 유형인지 선택하기 위함
     */

    @Column(name = "accident_disaster_type") // 사고/재난
    private String precedingType;

    @Column(name = "accident_type") // 사고 유형
    private String accidentType;

    @Column(name = "disaster_type") // 재난 유형
    private String disasterType;

    @Column(name = "scenario_port_area") // 사고 항만 구역
    private String portArea;

    @Column(name = "scenario_response_stage") // 사고 대응 단계
    private String responseStage;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true) // 안전 사고 항만 설비
    private List<AccidentPortFacility> accidentPortFacilityList = new ArrayList<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true) // 안전 사고 대응 활동
    private List<AccidentResponseActivity> accidentResponseActivityList = new ArrayList<>();

    @OneToOne(mappedBy = "scenario", fetch = FetchType.LAZY) // 시나리오 평가
    private ScenarioEvaluation scenarioEvaluation;

    @Builder
    public Scenario(Integer id, String name, String level, String impact, String precedingType, String accidentType,
                    String disasterType, String portArea, String responseStage,
                    List<AccidentPortFacility> accidentPortFacilityList,
                    List<AccidentResponseActivity> accidentResponseActivityList,
                    ScenarioEvaluation scenarioEvaluation) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.impact = impact;
        this.precedingType = precedingType;
        this.accidentType = accidentType;
        this.disasterType = disasterType;
        this.portArea = portArea;
        this.responseStage = responseStage;
        this.accidentPortFacilityList = accidentPortFacilityList;
        this.accidentResponseActivityList = accidentResponseActivityList;
        this.scenarioEvaluation = scenarioEvaluation;
    }

    @Transactional(readOnly = true)
    public void addAccidentPortFacility(List<AccidentPortFacilityDto> accidentPortFacilityDtoList) {
        for (AccidentPortFacilityDto accidentPortFacilityDto : accidentPortFacilityDtoList) {
            accidentPortFacilityDto.setScenario(this);
            this.accidentPortFacilityList.add(accidentPortFacilityDto.toEntity());
        }
    }

    @Transactional(readOnly = true)
    public void addAccidentResponseActivity(List<AccidentResponseActivityDto> accidentResponseActivityDtoList) {
        for (AccidentResponseActivityDto accidentResponseActivityDto : accidentResponseActivityDtoList) {
            accidentResponseActivityDto.setScenario(this);
            this.accidentResponseActivityList.add(accidentResponseActivityDto.toEntity());
        }
    }

    @Transactional
    public void removeAccidentPortFacility(List<AccidentPortFacility> accidentPortFacilityList) {
        for (AccidentPortFacility accidentPortFacility : accidentPortFacilityList) {
            this.accidentPortFacilityList.remove(accidentPortFacility);
        }
    }

    @Transactional
    public void removeAccidentResponseActivity(List<AccidentResponseActivity> accidentResponseActivityList) {
        for (AccidentResponseActivity accidentResponseActivity : accidentResponseActivityList) {
            this.accidentResponseActivityList.remove(accidentResponseActivity);
        }
    }

    @Transactional(readOnly = true)
    public void update(ScenarioDto scenarioDto) {
        this.level = scenarioDto.getLevel();
        this.impact = scenarioDto.getImpact();
        this.precedingType = scenarioDto.getPrecedingType();
        this.accidentType = scenarioDto.getAccidentType();
        this.disasterType = scenarioDto.getDisasterType();
        this.portArea = scenarioDto.getPortArea();
        this.responseStage = scenarioDto.getResponseStage();
    }
}
