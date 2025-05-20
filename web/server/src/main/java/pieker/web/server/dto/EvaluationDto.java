package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Evaluation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationDto {

    private Long id;
    private AssertableDto assertable;
    private boolean success;
    private String errorMessage;
    private String assertType;
    private String assertExpression;

    public static EvaluationDto toEvaluationDto(Evaluation evaluation){
        return EvaluationDto.builder()
                .id(evaluation.getId())
                .assertable(AssertableDto.toAssertionDto(evaluation.getAssertable()))
                .success(evaluation.isSuccess())
                .errorMessage(evaluation.getErrorMessage())
                .assertType(evaluation.getAssertType())
                .assertExpression(evaluation.getAssertExpression())
                .build();
    }
}
