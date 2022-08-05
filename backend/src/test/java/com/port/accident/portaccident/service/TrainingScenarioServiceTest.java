package com.port.accident.portaccident.service;

import com.port.accident.portaccident.domain.training_scenario.Scenario;
import com.port.accident.portaccident.domain.training_scenario.elements.AccidentPortFacility;
import com.port.accident.portaccident.dto.training_scenario.ScenarioAccidentPortFacilityDto;
import com.port.accident.portaccident.dto.training_scenario.ScenarioDto;
import com.port.accident.portaccident.dto.training_scenario.ScenarioSearchCondition;
import com.port.accident.portaccident.dto.training_scenario.elements.AccidentPortFacilityDto;
import com.port.accident.portaccident.enums.IncidentImpact;
import com.port.accident.portaccident.enums.IncidentLevel;
import com.port.accident.portaccident.enums.IncidentType;
import com.port.accident.portaccident.repository.training_scenario.AccidentPortFacilityRepository;
import com.port.accident.portaccident.repository.training_scenario.AccidentResponseActivityRepository;
import com.port.accident.portaccident.repository.training_scenario.ScenarioRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
//@Rollback(value = false)
public class TrainingScenarioServiceTest {

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    AccidentPortFacilityRepository accidentPortFacilityRepository;

    @Autowired
    AccidentResponseActivityRepository accidentResponseActivityRepository;


    @Test
    public void 시나리오_등록() {
        //Given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .incidentLevel(IncidentLevel.LEVEL_3)
                .incidentImpact(IncidentImpact.INCIDENT_IMPACT_A)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType("추락")
                .portArea("무역항 수상구역")
                .responseStage("2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("크레인")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .build();

        //When
        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);
        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);

        //Then
        Scenario scenario = scenarioRepository.findById(scenarioId).get();
        List<Scenario> scenarioList = scenarioRepository.findAll();
        assertEquals(1, scenarioList.size());
        assertEquals(scenarioDto.getName(), scenario.getName());

        List<AccidentPortFacility> accidentPortFacilityList = scenario.getAccidentPortFacilityList();
        assertEquals(2, accidentPortFacilityList.size());
        assertEquals(scenarioId, accidentPortFacilityList.get(0).getScenario().getId());
        assertEquals(scenarioId, accidentPortFacilityList.get(1).getScenario().getId());
        assertEquals(accidentPortFacilityDto.getName(), accidentPortFacilityList.get(0).getName());
        assertEquals(accidentPortFacilityDto2.getName(), accidentPortFacilityList.get(1).getName());


        assertEquals(scenarioDto.getAccidentPortFacilityList().get(0).getName(), accidentPortFacilityList.get(0).getName());
        assertEquals(scenarioDto.getAccidentPortFacilityList().get(1).getName(), accidentPortFacilityList.get(1).getName());
    }

    @Test(expected = IllegalStateException.class)
    public void 시나리오_중복_예외() {
        //Given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        ScenarioDto scenarioDto2 = ScenarioDto.builder()
                .name("SY2")
                .build();

        //When
        scenarioService.saveScenario(scenarioDto);
        scenarioService.saveScenario(scenarioDto2); // 예외 발생

        //Then
        fail("이미 존재하는 시나리오입니다.");
    }

    @Test
    public void 시나리오_수정() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .incidentLevel(IncidentLevel.LEVEL_3)
                .incidentImpact(IncidentImpact.INCIDENT_IMPACT_A)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType("추락")
                .portArea("무역항 수상구역")
                .responseStage("2")
                .build();

        Integer scenarioId = scenarioService.saveScenario(scenarioDto);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();

        ScenarioDto updateScenarioDto = ScenarioDto.builder()
                .id(scenario.getId())
                .name(scenario.getName())
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.INCIDENT_IMPACT_B)
                .incidentType(scenario.getIncidentType())
                .incidentDetailType(scenario.getIncidentDetailType())
                .portArea(scenario.getPortArea())
                .responseStage("3")
                .build();

        //when
        Integer updateScenarioId = scenarioService.updateScenario(updateScenarioDto);

        //then
        Scenario updateScenario = scenarioRepository.findById(updateScenarioId).get();

        assertEquals(scenarioId, updateScenarioId);
        assertEquals(updateScenarioDto.getName(), updateScenario.getName());
        assertEquals(updateScenarioDto.getIncidentImpact(), updateScenario.getIncidentImpact());
        assertEquals(updateScenarioDto.getIncidentDetailType(), updateScenario.getIncidentDetailType());
    }

    @Test
    public void 안전사고_항만설비_수정() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("크레인")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();


        AccidentPortFacilityDto updateAccidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("지게차")
                .scenario(scenario)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("사다리")
                .scenario(scenario)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto3 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .scenario(scenario)
                .build();

        List<AccidentPortFacilityDto> updateAccidentPortFacilityDtoList = new ArrayList<>();
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto2);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto3);

        //when
        Integer updateScenarioId = scenarioService.updateAccidentPortFacility(updateAccidentPortFacilityDtoList);

        //then
        List<AccidentPortFacility> updateAccidentPortFacilityList = scenario.getAccidentPortFacilityList();

        assertEquals(updateAccidentPortFacilityDto.getName(), updateAccidentPortFacilityList.get(0).getName());
        assertEquals(updateAccidentPortFacilityDto2.getName(), updateAccidentPortFacilityList.get(1).getName());
        assertEquals(updateAccidentPortFacilityDto3.getName(), updateAccidentPortFacilityList.get(2).getName());

        assertEquals(3, updateAccidentPortFacilityList.size());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(0).getScenario().getId());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(1).getScenario().getId());
    }

/*
    @Test
    public void 안전사고_대응활동_수정() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("크레인")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();


        AccidentResponseActivityDto updateAccidentResponseActivityDto = AccidentResponseActivityDto.builder()
                .comment("사고가 발생한 상황을 가정하여 상세하게 작성.")
                .manager("이혜원")
                .completePlaningTime(LocalDateTime.now())
                .build();

        List<AccidentResponseActivityDto> updateAccidentResponseActivityDtoList = new ArrayList<>();
        updateAccidentResponseActivityDtoList.add(updateAccidentResponseActivityDto);

        //when
        Integer updateScenarioId = scenarioService.modifyAccidentResponseActivity(scenario, updateAccidentResponseActivityDtoList);

        //then
        List<AccidentResponseActivity> updateScenarioAccidentResponseActivityList = scenario.getAccidentResponseActivityList();

        assertEquals(updateAccidentResponseActivityDto.getManager(), updateAccidentResponseActivityDtoList.get(0).getManager());
        assertEquals(1, updateScenarioAccidentResponseActivityList.size());
        assertEquals(updateScenarioId, updateScenarioAccidentResponseActivityList.get(0).getScenario().getId());
    }
*/

    @Test
    @Rollback(value = false)
    public void 시나리오_안전사고_항만설비_수정() {
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY1")
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.INCIDENT_IMPACT_B)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType("추락")
                .portArea("무역항 수상구역")
                .responseStage("3")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("크레인")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();


        ScenarioDto updateScenarioDto = ScenarioDto.builder()
                .id(scenario.getId())
                .name(scenario.getName())
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.INCIDENT_IMPACT_B)
                .incidentType(scenario.getIncidentType())
                .incidentDetailType(scenario.getIncidentDetailType())
                .portArea(scenario.getPortArea())
                .responseStage("3")
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("지게차")
                .scenario(scenario)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("사다리")
                .scenario(scenario)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto3 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .scenario(scenario)
                .build();

        List<AccidentPortFacilityDto> updateAccidentPortFacilityDtoList = new ArrayList<>();
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto2);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto3);

        //when
        Integer updateScenarioId = scenarioService.modifyScenario(updateScenarioDto, updateAccidentPortFacilityDtoList);

        //then
        Scenario updateScenario = scenarioRepository.findById(updateScenarioId).get();

        assertEquals(scenarioId, updateScenarioId);
        assertEquals(updateScenarioDto.getName(), updateScenario.getName());
        assertEquals(updateScenarioDto.getIncidentImpact(), updateScenario.getIncidentImpact());
        assertEquals(updateScenarioDto.getIncidentDetailType(), updateScenario.getIncidentDetailType());

        List<AccidentPortFacility> updateAccidentPortFacilityList = scenario.getAccidentPortFacilityList();

        assertEquals(updateAccidentPortFacilityDto.getName(), updateAccidentPortFacilityList.get(0).getName());
        assertEquals(updateAccidentPortFacilityDto2.getName(), updateAccidentPortFacilityList.get(1).getName());
        assertEquals(updateAccidentPortFacilityDto3.getName(), updateAccidentPortFacilityList.get(2).getName());

        assertEquals(3, updateAccidentPortFacilityList.size());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(0).getScenario().getId());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(1).getScenario().getId());

        assertEquals(updateScenario.getAccidentPortFacilityList().get(0).getName(), updateAccidentPortFacilityList.get(0).getName());
        assertEquals(updateScenario.getAccidentPortFacilityList().get(1).getName(), updateAccidentPortFacilityList.get(1).getName());

    }

    @Test
    public void 시나리오_삭제() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name("크레인")
                .scenario(scenarioDto.toEntity())
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name("컨테이너")
                .scenario(scenarioDto.toEntity())
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);

        //when
        scenarioService.deleteScenario(scenarioId);

        //then
        Optional<Scenario> deleteScenario = scenarioRepository.findById(scenarioId);
        assertFalse(deleteScenario.isPresent());
        assertEquals(0, accidentPortFacilityRepository.findByScenarioId(scenarioId).size());
    }

    @Test
    public void 시나리오_조회_페이징() {
        //given
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ScenarioDto scenarioDto = ScenarioDto.builder()
                    .name("SY" + i)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                    .name("크레인" + i)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                    .name("컨테이너" + i)
                    .build();

            List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
            accidentPortFacilityDtoList.add(accidentPortFacilityDto);
            accidentPortFacilityDtoList.add(accidentPortFacilityDto2);
            scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);
        });

        ScenarioSearchCondition condition = new ScenarioSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 3); // Sort.by(Sort.Direction.DESC, "name")

        //when
        Page<ScenarioAccidentPortFacilityDto> scenario = scenarioService.searchPageScenario(condition, pageRequest);

        //then
        List<ScenarioAccidentPortFacilityDto> content = scenario.getContent();
        assertEquals("조회된 데이터 수", 3, content.size());
        assertEquals("전체 데이터 수", 5, scenario.getTotalElements());
        assertEquals("페이지 번호", 0, scenario.getNumber());
        assertEquals("전체 페이지 번호", 2, scenario.getTotalPages());
        assertTrue("첫번째 항목인가?", scenario.isFirst());
        assertTrue("다음 페이지가 있는가?", scenario.hasNext());
        assertEquals("SY1", content.get(0).getName());
    }

    @Test
    public void 시나리오_검색_페이징() {
        //given
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ScenarioDto scenarioDto = ScenarioDto.builder()
                    .name("SY" + i)
                    .build();

            scenarioService.saveScenario(scenarioDto);

            ScenarioDto scenarioDto2 = ScenarioDto.builder()
                    .name("SN" + i)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                    .name("크레인" + i)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                    .name("컨테이너" + i)
                    .build();

            List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
            accidentPortFacilityDtoList.add(accidentPortFacilityDto);
            accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

            scenarioService.registerScenario(scenarioDto2, accidentPortFacilityDtoList);

        });

        ScenarioSearchCondition condition = new ScenarioSearchCondition();
        condition.setName("SN");
        PageRequest pageRequest = PageRequest.of(0, 3);

        //when
        Page<ScenarioAccidentPortFacilityDto> scenario = scenarioService.searchPageScenario(condition, pageRequest);

        //then
        List<ScenarioAccidentPortFacilityDto> content = scenario.getContent();
        assertEquals("조회된 데이터 수", 3, content.size());
        assertEquals("전체 데이터 수", 5, scenario.getTotalElements());
        assertEquals("페이지 번호", 0, scenario.getNumber());
        assertEquals("전체 페이지 번호", 2, scenario.getTotalPages());
        assertTrue("첫번째 항목인가?", scenario.isFirst());
        assertTrue("다음 페이지가 있는가?", scenario.hasNext());
        assertEquals("SN1", content.get(0).getName());
    }
}