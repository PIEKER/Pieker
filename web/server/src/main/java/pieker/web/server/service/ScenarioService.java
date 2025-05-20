package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import pieker.web.server.dto.ScenarioDto;
import pieker.web.server.repository.ScenarioRepository;

import java.util.List;

public class ScenarioService {

    private ScenarioRepository scenarioRepository;

    @Autowired
    public ScenarioService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public List<ScenarioDto> getScenarios(){
        return this.scenarioRepository.findAll().stream().map(ScenarioDto::toScenarioDto).toList();
    }
}
