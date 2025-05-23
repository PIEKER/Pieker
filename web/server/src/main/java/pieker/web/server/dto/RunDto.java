package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Evaluation;
import pieker.web.server.dbo.Run;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
        Map<Long, List<EvaluationDto>> assertToEvaluationMap = new HashMap<>();
        for (Evaluation evaluation : evaluations){
            if (assertToEvaluationMap.containsKey(evaluation.getAssertion().getId())){
                assertToEvaluationMap.get(evaluation.getAssertion().getId()).add(EvaluationDto.toEvaluationDto(evaluation));
            } else {
                List<EvaluationDto> evaluationDtoList = new ArrayList<>();
                evaluationDtoList.add(EvaluationDto.toEvaluationDto(evaluation));
                assertToEvaluationMap.put(evaluation.getAssertion().getId(), evaluationDtoList);
            }
        }
        this.steps.forEach(stepDto -> stepDto.mapEvaluationsToAssertions(assertToEvaluationMap));
    }
}
