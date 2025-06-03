package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pieker.web.server.dbo.Assertion;
import pieker.web.server.dbo.Evaluation;
import pieker.web.server.dbo.Run;
import pieker.web.server.dbo.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RunDto {

    private Long id;

    private String name;
    private Long scenarioId;
    private String scenarioName;
    private List<StepDto> steps;

    public RunDto toRunDto(Run run) {
        RunDto tmp = RunDto.builder()
                .id(run.getId())
                .name(run.getName())
                .scenarioId(run.getScenario().getId())
                .scenarioName(run.getScenario().getName())
                .steps(run.getScenario().getSteps().stream().map(StepDto::toStepDto).toList())
                .build();

        tmp.getStepDtoFromEvaluationList(run.getEvaluations());
        return tmp;
    }

    private void getStepDtoFromEvaluationList(List<Evaluation> evaluations) {
        // map every
        Map<Long, List<AssertionDto>> stepToAssertionMap = new HashMap<>();
        for (Evaluation evaluation : evaluations){
            Assertion assertion = evaluation.getAssertion();
            assert assertion != null;
            Step assertStep = assertion.getStep();
            assert assertStep != null;

            AssertionDto assDto = AssertionDto.toAssertionDto(assertion);
            EvaluationDto evalDto = EvaluationDto.toEvaluationDto(evaluation);
            assDto.setEvaluations(List.of(evalDto));
            if (stepToAssertionMap.containsKey(assertStep.getId())){
                log.info("mapping assertion {} to step {}", assDto.getId(), assertStep.getId());
                stepToAssertionMap.get(assertStep.getId()).add(assDto);
            } else {
                log.info("mapping new assertionList {} to step {}", assDto.getId(), assertStep.getId());
                stepToAssertionMap.put(assertStep.getId(), new ArrayList<>(List.of(assDto)));
            }
        }

        this.steps.forEach(stepDto -> {
            List<AssertionDto> l = stepToAssertionMap.get(stepDto.getId());
            if (l == null){log.info("no assertions found for step {}", stepDto.getId());return;}
            log.info("mapping assertList {} to step {}", l.stream().map(AssertionDto::getId).toList(), stepDto.getId());
            stepDto.setAssertions(l);
        });
    }
}
