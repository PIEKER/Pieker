package pieker.dsl.model;

import lombok.Getter;
import lombok.Setter;
import pieker.api.assertions.Assert;
import pieker.api.Assertion;
import pieker.api.assertions.StubAssert;
import pieker.dsl.architecture.component.StepComponent;
import pieker.dsl.architecture.exception.PiekerProcessingException;
import pieker.dsl.architecture.preprocessor.Validator;

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
            if (!(ass instanceof StubAssert)){
                Validator.isIdentifierPresent(ass.getIdentifier());
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

        this.assertList.forEach(Assert::processAssert);
    }

    protected List<Assertion> getEvaluationList(){
        return new ArrayList<>(this.assertList);
    }
}
