package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Scenario;
import pieker.web.server.dto.ScenarioDto;
import pieker.web.server.repository.ScenarioRepository;

import java.util.List;

@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioService(ScenarioRepository scenarioRepository, StepService stepService) {
        this.scenarioRepository = scenarioRepository;
    }

    public List<ScenarioDto> getScenarios(){
        return this.scenarioRepository.findAll().stream().map(ScenarioDto::toScenarioDto).toList();
    }

    public Scenario createScenario(String name){
        return this.scenarioRepository.save(Scenario.builder().name(name).build());
    }

    public Scenario findByName(String name){
        return this.scenarioRepository.findByName(name).orElse(null);
    }
}
