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
    private boolean success;
    private String errorMessage;

    public static EvaluationDto toEvaluationDto(Evaluation evaluation){
        return EvaluationDto.builder()
                .id(evaluation.getId())
                .success(evaluation.isSuccess())
                .errorMessage(evaluation.getErrorMessage())
                .build();
    }
}
