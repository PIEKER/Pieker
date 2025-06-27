package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.api.assertions.Assert;
import pieker.api.Assertion;
import pieker.api.assertions.StubAssert;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.template.component.StepComponent;
import pieker.dsl.architecture.exception.PiekerProcessingException;
import pieker.dsl.architecture.preprocessor.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class Then {

    private final Step step;
    private int line;
    private String description;

    private final List<Assert> assertList = new ArrayList<>();
    private List<String> logAllList = new ArrayList<>();

    public Then(Step step) {
        this.step = step;
    }

    public void validateLogAllList(){
        this.logAllList.forEach(Validator::isIdentifierPresent);
    }

    public void validateAssertList() {
        this.assertList.forEach(ass -> {
            if (!(ass instanceof StubAssert)){
                try{
                    Validator.isIdentifierPresent(ass.getIdentifier());
                } catch (ValidationException e){
                    log.debug("unidentified assertion target. If this identifier is no storage component, refactor the configuration. Message: {}", e.getMessage());
                }
            }
            ass.validate(this.line);
        });
    }

    public void addLogAllIdentifier(String identifier){
        this.logAllList.add(identifier.trim());
    }

    public void addAssert(Assert ass){
        this.assertList.add(ass);
    }

    public void processAssertList() {
        // Log All
        Map<String, StepComponent> stepComponentMap = this.step.getTestComponentMap();
        this.logAllList.forEach(component -> {
            if (stepComponentMap.containsKey(component)) stepComponentMap.get(component).enableLogging();
            else throw new PiekerProcessingException("unknown component identifier provided. Logging could not be enabled");
        });

        this.assertList.forEach(ass -> {
            if (ass instanceof StubAssert) return;
            if (stepComponentMap.containsKey(ass.getIdentifier())) stepComponentMap.get(ass.getIdentifier()).enableLogging();
            else log.debug("unknown component identifier provided. Logging could not be enabled for {}", ass.getIdentifier());
        });

        this.assertList.forEach(Assert::processAssert);
    }

    protected List<Assertion> getEvaluationList(){
        return new ArrayList<>(this.assertList);
    }
}
