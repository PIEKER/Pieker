package pieker.dsl.code.strategy.architecture;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.SupervisorTraffic;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;
import pieker.dsl.code.template.architecture.Sql;

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
