package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Scenario;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScenarioDto {

    private Long id;
    private String name;

    // Step configurations
    private List<RunDto> runs;

    public static ScenarioDto toScenarioDto(Scenario scenario) {
        return ScenarioDto.builder()
                .id(scenario.getId())
                .name(scenario.getName())
                .runs(scenario.getRuns().stream().map(RunDto::toRunDto).toList())
                .build();
    }

}
