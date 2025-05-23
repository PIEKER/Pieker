package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Assertion;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssertionDto {

    private Long id;

    private String identifier;
    private String assertType;
    private String assertExpression;
    private List<EvaluationDto> evaluations;

    public static AssertionDto toAssertionDto(Assertion assertion){
        return AssertionDto.builder()
                .id(assertion.getId())
                .identifier(assertion.getIdentifier())
                .assertType(assertion.getAssertType())
                .assertExpression(assertion.getAssertExpression())
                .evaluations(new ArrayList<>())
                .build();
    }
}
