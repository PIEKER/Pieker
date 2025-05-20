package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Evaluation;
import pieker.web.server.dbo.Run;

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

    public static RunDto toRunDto(Run run) {
        RunDto tmp = RunDto.builder()
                .id(run.getId())
                .name(run.getName())
                .scenarioId(run.getScenario().getId())
                .scenarioName(run.getScenario().getName())
                .build();

        tmp.setSteps(getStepDtoFromEvaluationList(run.getEvaluations()));
        return tmp;
    }

    private static List<StepDto> getStepDtoFromEvaluationList(List<Evaluation> evaluations) {
        Map<Long, StepDto> stepDtoMap = new HashMap<>();
        for (Evaluation evaluation : evaluations) {
            StepDto stepDto = StepDto.toStepDto(evaluation.getStep());
            if (!stepDtoMap.containsKey(stepDto.getId())) {
                stepDto.getEvaluations().add(EvaluationDto.toEvaluationDto(evaluation));
                stepDtoMap.put(stepDto.getId(), stepDto);
            } else {
                stepDtoMap.get(stepDto.getId()).getEvaluations().add(EvaluationDto.toEvaluationDto(evaluation));
            }
        }
        return List.copyOf(stepDtoMap.values());
    }
}
