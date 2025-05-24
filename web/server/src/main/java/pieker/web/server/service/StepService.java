package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Scenario;
import pieker.web.server.dbo.Step;
import pieker.web.server.dto.StepDto;
import pieker.web.server.repository.StepRepository;

@Service
public class StepService {

    private final StepRepository stepRepository;

    @Autowired
    public StepService(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    public Step getStep(String identifier){
        return this.stepRepository.findByIdentifier(identifier).orElse(null);
    }

    public Step createStep(StepDto stepDto, Scenario scenario){
        return this.stepRepository.save(
                Step.builder()
                        .name(stepDto.getName())
                        .identifier(stepDto.getIdentifier())
                        .scenario(scenario)
                        .build()
        );
    }
}
