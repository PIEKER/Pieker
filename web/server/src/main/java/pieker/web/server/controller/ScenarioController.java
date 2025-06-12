package pieker.web.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pieker.web.server.dto.ScenarioDto;
import pieker.web.server.service.ScenarioService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // allow frontend origin
@RestController
@RequestMapping("/scenarios")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;

    @GetMapping("/{id}")
    public ScenarioDto getScenario(@PathVariable Long id) {
        return this.scenarioService.getScenarioById(id);
    }

    @GetMapping
    public List<ScenarioDto> getAllScenario(){
        return this.scenarioService.getAllMinimalScenario();
    }
}
