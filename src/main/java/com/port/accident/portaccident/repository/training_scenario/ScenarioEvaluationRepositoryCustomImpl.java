package com.port.accident.portaccident.repository.training_scenario;

import com.port.accident.portaccident.domain.training_scenario.scenario_evaluation.ScenarioEvaluation;
import com.port.accident.portaccident.dto.training_scenario_result.EvaluationSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.port.accident.portaccident.domain.training_scenario.QScenario.scenario;
import static org.springframework.util.ObjectUtils.isEmpty;
import static com.port.accident.portaccident.domain.training_scenario.scenario_evaluation.QScenarioEvaluation.scenarioEvaluation;


@Repository
@Transactional
@RequiredArgsConstructor
public class ScenarioEvaluationRepositoryCustomImpl implements ScenarioEvaluationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ScenarioEvaluation> searchPageScenarioEvaluation(EvaluationSearchCondition condition, Pageable pageable) {
        List<ScenarioEvaluation> content = queryFactory
                .selectFrom(scenarioEvaluation)
                .where(nameContains(condition.getName()))
                .orderBy(scenarioEvaluation.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(scenarioEvaluation.count())
                .from(scenarioEvaluation)
                .where(nameContains(condition.getName()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameContains(String nameCondition) {
        return isEmpty(nameCondition) ? null : scenarioEvaluation.name.contains(nameCondition);
    }
}
