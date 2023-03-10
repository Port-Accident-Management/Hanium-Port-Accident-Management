package com.port.accident.portaccident.service.training_scenario;

import com.port.accident.portaccident.domain.training_scenario.Scenario;
import com.port.accident.portaccident.domain.training_scenario.elements.AccidentPortFacility;
import com.port.accident.portaccident.domain.training_scenario.elements.AccidentResponseActivity;
import com.port.accident.portaccident.dto.training_scenario.ScenarioAccidentPortFacilityDto;
import com.port.accident.portaccident.dto.training_scenario.ScenarioDto;
import com.port.accident.portaccident.dto.training_scenario.ScenarioSearchCondition;
import com.port.accident.portaccident.dto.training_scenario.elements.AccidentPortFacilityDto;
import com.port.accident.portaccident.dto.training_scenario.elements.AccidentResponseActivityDto;
import com.port.accident.portaccident.enums.*;
import com.port.accident.portaccident.repository.training_scenario.AccidentPortFacilityRepository;
import com.port.accident.portaccident.repository.training_scenario.AccidentResponseActivityRepository;
import com.port.accident.portaccident.repository.training_scenario.ScenarioRepository;
import com.port.accident.portaccident.service.ScenarioService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public void ????????????_??????() {
        //Given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .incidentLevel(IncidentLevel.LEVEL_3)
                .incidentImpact(IncidentImpact.DAMAGE)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType(IncidentDetailType.DROP)
                .portArea(TrainingPlace.PLACE1)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.CRANE)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
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
    public void ????????????_??????_??????() {
        //Given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        ScenarioDto scenarioDto2 = ScenarioDto.builder()
                .name("SY2")
                .build();

        //When
        scenarioService.saveScenario(scenarioDto);
        scenarioService.saveScenario(scenarioDto2); // ?????? ??????

        //Then
        fail("?????? ???????????? ?????????????????????.");
    }

    @Test
    public void ????????????_??????() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .incidentLevel(IncidentLevel.LEVEL_3)
                .incidentImpact(IncidentImpact.DAMAGE)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType(IncidentDetailType.DROP)
                .portArea(TrainingPlace.PLACE1)
                .build();

        Integer scenarioId = scenarioService.saveScenario(scenarioDto);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();

        ScenarioDto updateScenarioDto = ScenarioDto.builder()
                .id(scenario.getId())
                .name(scenario.getName())
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.SLIGHT)
                .incidentType(scenario.getIncidentType())
                .incidentDetailType(scenario.getIncidentDetailType())
                .portArea(scenario.getPortArea())
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
    public void ????????????_????????????_??????() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.CRANE)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);
        Scenario scenario = scenarioRepository.findById(scenarioId).get();


        AccidentPortFacilityDto updateAccidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.FORKLIFT)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.LADDER)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto3 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
                .build();

        List<AccidentPortFacilityDto> updateAccidentPortFacilityDtoList = new ArrayList<>();
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto2);
        updateAccidentPortFacilityDtoList.add(updateAccidentPortFacilityDto3);

        //when
        Integer updateScenarioId = scenarioService.updateAccidentPortFacility(scenarioId, updateAccidentPortFacilityDtoList);

        //then
        List<AccidentPortFacility> updateAccidentPortFacilityList = scenario.getAccidentPortFacilityList();

        assertEquals(updateAccidentPortFacilityDto.getName(), updateAccidentPortFacilityList.get(0).getName());
        assertEquals(updateAccidentPortFacilityDto2.getName(), updateAccidentPortFacilityList.get(1).getName());
        assertEquals(updateAccidentPortFacilityDto3.getName(), updateAccidentPortFacilityList.get(2).getName());

        assertEquals(3, updateAccidentPortFacilityList.size());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(0).getScenario().getId());
        assertEquals(updateScenarioId, updateAccidentPortFacilityList.get(1).getScenario().getId());
    }

    @Test
    @Rollback(value = false)
    public void ????????????_????????????_????????????_??????() {
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY1")
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.SLIGHT)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType(IncidentDetailType.DROP)
                .portArea(TrainingPlace.PLACE1)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.CRANE)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
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
                .incidentImpact(IncidentImpact.SLIGHT)
                .incidentType(scenario.getIncidentType())
                .incidentDetailType(scenario.getIncidentDetailType())
                .portArea(scenario.getPortArea())
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.FORKLIFT)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.LADDER)
                .build();

        AccidentPortFacilityDto updateAccidentPortFacilityDto3 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
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
    public void ????????????_??????() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY2")
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.CRANE)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
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
    public void ????????????_??????_?????????() {
        //given
        IntStream.rangeClosed(1, 5).forEach(i -> {
            ScenarioDto scenarioDto = ScenarioDto.builder()
                    .name("SY" + i)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                    .name(PortFacility.CRANE)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                    .name(PortFacility.CONTAINER)
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
        assertEquals("????????? ????????? ???", 3, content.size());
        assertEquals("?????? ????????? ???", 5, scenario.getTotalElements());
        assertEquals("????????? ??????", 0, scenario.getNumber());
        assertEquals("?????? ????????? ??????", 2, scenario.getTotalPages());
        assertTrue("????????? ?????????????", scenario.isFirst());
        assertTrue("?????? ???????????? ??????????", scenario.hasNext());
        assertEquals("SY1", content.get(0).getName());
    }

    @Test
    public void ????????????_??????_?????????() {
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
                    .name(PortFacility.CRANE)
                    .build();

            AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                    .name(PortFacility.CONTAINER)
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
        assertEquals("????????? ????????? ???", 3, content.size());
        assertEquals("?????? ????????? ???", 5, scenario.getTotalElements());
        assertEquals("????????? ??????", 0, scenario.getNumber());
        assertEquals("?????? ????????? ??????", 2, scenario.getTotalPages());
        assertTrue("????????? ?????????????", scenario.isFirst());
        assertTrue("?????? ???????????? ??????????", scenario.hasNext());
        assertEquals("SN1", content.get(0).getName());
    }

    @Test
    public void ????????????_??????_??????() {
        //given
        ScenarioDto scenarioDto = ScenarioDto.builder()
                .name("SY1")
                .incidentLevel(IncidentLevel.LEVEL_1)
                .incidentImpact(IncidentImpact.SLIGHT)
                .incidentType(IncidentType.INCIDENT)
                .incidentDetailType(IncidentDetailType.DROP)
                .portArea(TrainingPlace.PLACE1)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto = AccidentPortFacilityDto.builder()
                .name(PortFacility.CRANE)
                .build();

        AccidentPortFacilityDto accidentPortFacilityDto2 = AccidentPortFacilityDto.builder()
                .name(PortFacility.CONTAINER)
                .build();

        List<AccidentPortFacilityDto> accidentPortFacilityDtoList = new ArrayList<>();
        accidentPortFacilityDtoList.add(accidentPortFacilityDto);
        accidentPortFacilityDtoList.add(accidentPortFacilityDto2);

        Integer scenarioId = scenarioService.registerScenario(scenarioDto, accidentPortFacilityDtoList);

        AccidentResponseActivityDto accidentResponseActivityDto = AccidentResponseActivityDto.builder()
                .comment("????????? ????????? ????????? ???????????? ???????????? ??????.")
                .manager("?????????")
                .completePlaningTime(LocalDateTime.now())
                .build();

        AccidentResponseActivityDto accidentResponseActivityDto2 = AccidentResponseActivityDto.builder()
                .comment("????????? ????????? ????????? ???????????? ???????????? ??????.")
                .manager("?????????")
                .completePlaningTime(LocalDateTime.now())
                .build();

        scenarioService.registerAccidentResponseActivity(scenarioId, accidentResponseActivityDto);
        scenarioService.registerAccidentResponseActivity(scenarioId, accidentResponseActivityDto2);

        //when
        List<PortFacility> portFacilityNameList = scenarioService.findAccidentPortFacilityNameByScenarioId(scenarioId);
        List<AccidentResponseActivity> accidentResponseActivityList = scenarioService.findAccidentResponseActivityByScenarioId(scenarioId);

        //then
        assertEquals(2, portFacilityNameList.size());
        assertEquals(2, accidentResponseActivityList.size());
        assertEquals(accidentPortFacilityDto.getName().getFacilityValue(), portFacilityNameList.get(0).getFacilityValue());
        assertEquals(accidentPortFacilityDto2.getName().getFacilityValue(), portFacilityNameList.get(1).getFacilityValue());
        assertEquals(accidentResponseActivityDto.getManager(), accidentResponseActivityList.get(0).getManager());
        assertEquals(accidentResponseActivityDto2.getManager(), accidentResponseActivityList.get(1).getManager());
    }

}