package pieker.web.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pieker.web.server.dbo.Scenario;
import pieker.web.server.dto.ScenarioDto;
import pieker.web.server.repository.ScenarioRepository;

@CrossOrigin(origins = "http://localhost:5173") // allow frontend origin
@RestController
@RequestMapping("/scenarios")
public class ScenarioController {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @PostMapping
    public Scenario createScenario(@RequestBody Scenario scenario) {
        return scenarioRepository.save(scenario);
    }

    @GetMapping("/{id}")
    public ScenarioDto getScenario(@PathVariable Long id) {
        return ScenarioDto.toScenarioDto(
                scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found"))
        );
    }
}
