package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.api.assertions.Assert;
import pieker.api.Assertions;
import pieker.dsl.code.component.StepComponent;
import pieker.dsl.code.exception.PiekerProcessingException;
import pieker.dsl.code.preprocessor.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Then {

    private final Step step;
    private int line;
    private String description;

    int assertAfter = 0;
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
            Validator.isIdentifierPresent(ass.getIdentifier());
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

        this.assertList.forEach(Assert::processAssert);
    }

    protected List<Assertions> getEvaluationList(){
        return new ArrayList<>(this.assertList);
    }
}
