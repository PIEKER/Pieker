package pieker.web.server.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Step;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepDto {

    private Long id;

    private String name;
    private String identifier;
    private List<AssertionDto> assertions;

    public static StepDto toStepDto(Step step) {
        return StepDto.builder()
                .id(step.getId())
                .name(step.getName())
                .identifier(step.getIdentifier())
                .build();
    }

    protected void mapEvaluationsToAssertions(Map<Long, List<EvaluationDto>> assertToEvaluationMap){
        this.assertions.forEach(assertionDto -> assertionDto.setEvaluations(assertToEvaluationMap.get(assertionDto.getId())));
    }
}
