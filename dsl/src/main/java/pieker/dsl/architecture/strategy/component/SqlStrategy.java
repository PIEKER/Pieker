package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.component.SupervisorTraffic;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.architecture.template.traffic.Sql;

@Slf4j
public class SqlStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing SQL node...");
        this.checkArguments(args);
        SupervisorTraffic traffic = new SupervisorTraffic(args[0], new Sql(args[1], args[2]));
        Engine.getCurrentStep().addStepComponent(traffic.getIdentifier(), traffic);
        log.debug("added sql to supervisor in step '{}'", Engine.getCurrentStep().getName());
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        Validator.updateIdentifierSet(args[0]);
    }

    private void checkArguments(String[] args){
        if (args.length != 3){
            throw new ValidationException("Invalid amount of arguments on SQL keyword.");
        }
    }
}
