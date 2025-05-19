package pieker.web.server.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Step;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepDto {

    private Long id;

    private String name;        // Step name (e.g., "increment-counter")
    private String identifier;  // Some unique step logic reference

    private List<EvaluationDto> evaluations;

    public static StepDto toStepDto(Step step) {
        return StepDto.builder()
                .id(step.getId())
                .name(step.getName())
                .evaluations(new ArrayList<>())
                .build();
    }
}
