package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Assertion;
import pieker.web.server.dbo.Run;
import pieker.web.server.dbo.Scenario;
import pieker.web.server.dbo.Step;
import pieker.web.server.dto.RunDto;
import pieker.web.server.repository.RunRepository;

import java.util.List;
import java.util.Objects;

@Service
public class RunService {

    private final ScenarioService scenarioService;
    private final StepService stepService;
    private final AssertionService assertionService;
    private final EvaluationService evaluationService;
    private final RunRepository runRepository;

    @Autowired
    public RunService(ScenarioService scenarioService,
                      StepService stepService,
                      AssertionService assertionService,
                      EvaluationService evaluationService,
                      RunRepository runRepository) {
        this.scenarioService = scenarioService;
        this.stepService = stepService;
        this.assertionService = assertionService;
        this.evaluationService = evaluationService;
        this.runRepository = runRepository;
    }

    public List<RunDto> getRuns() {
        return runRepository.findAll().stream().map(run -> new RunDto().toRunDto(run)).toList();
    }

    public RunDto getRun(Long id) {
        return new RunDto().toRunDto(Objects.requireNonNull(runRepository.findById(id).orElse(null)));
    }

    public void createRun(RunDto runDto){

        Scenario scenario = this.scenarioService.findByName(runDto.getScenarioName());
        int nextRunNr = 1;
        if(scenario == null){
            scenario = this.scenarioService.createScenario(runDto.getScenarioName());
        } else {
            nextRunNr = this.runRepository.findAllByScenario(scenario).size() + 1;
        }

        assert scenario.getId() != null;
        Run run = this.runRepository.save(Run.builder()
                    .name(runDto.getScenarioName() + "-" + nextRunNr)
                    .scenario(scenario)
                    .build()
        );

        Scenario finalScenario = scenario;
        runDto.getSteps().forEach(stepDto -> {
            Step step = this.stepService.getStep(stepDto.getIdentifier());

            if (step == null){
                step = this.stepService.createStep(stepDto, finalScenario);
            }
            Step finalStep = step;
            stepDto.getAssertions().forEach(assDto -> {
                Assertion ass = this.assertionService.createAssertion(assDto, finalStep);
                assDto.getEvaluations().forEach(evalDto ->
                        this.evaluationService.createEvaluation(evalDto, run, ass));
            });
        });
    }
}
