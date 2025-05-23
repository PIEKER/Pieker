package pieker.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssertionDto {

    private String identifier;
    private String assertType;
    private String assertExpression;
    private List<EvaluationDto> evaluations;

}
