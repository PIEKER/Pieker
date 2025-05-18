package pieker.web.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pieker.web.server.dbo.Scenario;
import pieker.web.server.repository.ScenarioRepository;

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
    public Scenario getScenario(@PathVariable Long id) {
        return scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
    }
}
