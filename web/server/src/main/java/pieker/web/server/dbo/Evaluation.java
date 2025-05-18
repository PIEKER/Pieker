package pieker.web.server.dbo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Evaluation {

    private boolean success;
    private String errorMessage;
    private String assertType;
    private String assertExpression;
}
