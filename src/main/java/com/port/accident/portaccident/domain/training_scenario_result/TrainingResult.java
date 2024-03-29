package com.port.accident.portaccident.domain.training_scenario_result;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.port.accident.portaccident.domain.training_scenario.Scenario;
import com.port.accident.portaccident.domain.training_scenario_result.evaluation.TrainingByDate;
import com.port.accident.portaccident.domain.training_scenario_result.elements.TrainingParticipants;
import com.port.accident.portaccident.domain.training_scenario_result.elements.TrainingPortFacility;
import com.port.accident.portaccident.enums.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "training_result")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingResult {

    @Id
    @Column(name = "training_result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "training_name")
    private String name;

    @Column(name = "training_start_date")
    private LocalDateTime startDate;
    @Column(name = "training_end_date")
    private LocalDateTime endDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "training_place")
    private TrainingPlace place;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "training_type")
    private TrainingType trainingType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "accident_level")
    private IncidentLevel incidentLevel;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "incident_impact")
    private IncidentImpact incidentImpact;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "incident_type")
    private IncidentType incidentType;

    @Column(name = "incident_detail_type")
    private String incidentDetailType;

    @Column(name = "training_department")
    private String department;

    @Column(name = "training_participants")
    private String trainingParticipants;

    @Column(name = "training_port_area")
    private String trainingArea;    //훈련대상 항만구역

//    @JsonManagedReference
    @OneToMany(mappedBy = "trainingResult")
    private List<TrainingPortFacility> trainingPortFacilityList = new ArrayList<>();

    @OneToMany(mappedBy = "trainingResult")
    private List<TrainingByDate> trainingByDateList = new ArrayList<>();

//    @OneToMany(mappedBy = "trainingResult")
//    private List<TrainingParticipants> trainingParticipantsList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;


    @Builder
    public TrainingResult(Integer id, String name, LocalDateTime startDate, LocalDateTime endDate, TrainingPlace place, TrainingType trainingType, IncidentLevel incidentLevel, IncidentImpact incidentImpact, IncidentType incidentType, String incidentDetailType, String department, String trainingParticipants, String trainingArea, List<TrainingByDate> trainingByDateList, Scenario scenario) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.trainingType = trainingType;
        this.incidentLevel = incidentLevel;
        this.incidentImpact = incidentImpact;
        this.incidentType = incidentType;
        this.incidentDetailType = incidentDetailType;
        this.department = department;
        this.trainingParticipants = trainingParticipants;
        this.trainingArea = trainingArea;
        this.trainingByDateList = trainingByDateList;
        this.scenario = scenario;
    }


    public void updateTrainingPortFacilityList(TrainingPortFacility facility) {
        trainingPortFacilityList.add(facility);
    }

    public void updateTrainingByDateList(TrainingByDate byDate) {
        trainingByDateList.add(byDate);
    }
//
//    public void updateTrainingParticipantsList(TrainingParticipants participants) {
//        trainingParticipantsList.add(participants);
//    }

}
