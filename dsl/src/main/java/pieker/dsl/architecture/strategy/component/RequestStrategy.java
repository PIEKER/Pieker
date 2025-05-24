package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.template.traffic.SupervisorTraffic;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.architecture.template.traffic.Request;

@Slf4j
public class RequestStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("process REQUEST node...");
        this.checkArguments(args);
        SupervisorTraffic traffic = new SupervisorTraffic(args[0], new Request(args[1], args[2]));
        Engine.getCurrentStep().addStepComponent(traffic.getIdentifier(), traffic);
        log.debug("added request to supervisor in step '{}'", Engine.getCurrentStep().getName());
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        Validator.updateIdentifierSet(args[0]);
    }

    private void checkArguments(String[] args){
        if (args.length != 3){
            throw new ValidationException("Invalid amount of arguments on REQUEST keyword.");
        }
    }
}
