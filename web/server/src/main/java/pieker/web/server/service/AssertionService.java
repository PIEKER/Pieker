package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Assertion;
import pieker.web.server.dbo.Step;
import pieker.web.server.dto.AssertionDto;
import pieker.web.server.repository.AssertionRepository;

@Service
public class AssertionService {

    private final AssertionRepository assertionRepository;

    @Autowired
    private AssertionService(AssertionRepository assertionRepository){
        this.assertionRepository = assertionRepository;
    }

    public Assertion createAssertion(AssertionDto assertionDto, Step step){
        return this.assertionRepository
                .save(
                        Assertion.builder()
                                .identifier(assertionDto.getIdentifier())
                                .assertType(assertionDto.getAssertType())
                                .assertExpression(assertionDto.getAssertExpression())
                                .step(step)
                                .build()
        );
    }

}
