package pieker.web.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pieker.web.server.dbo.Assertable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssertableDto {

    private Long id;

    private String identifier;

    public static AssertableDto toAssertionDto(Assertable assertable){
        return AssertableDto.builder()
                .id(assertable.getId())
                .identifier(assertable.getIdentifier())
                .build();
    }
}
