package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.component.DatabaseProxy;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;

@Slf4j
public class DatabaseStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing DATABASE node...");
        this.checkArguments(args);

        DatabaseProxy databaseProxy = new DatabaseProxy(args[0]);
        Engine.getCurrentStep().addStepComponent(databaseProxy.getIdentifier(), databaseProxy);
        log.debug("added database-proxy for database '{}' in step '{}'", args[0], Engine.getCurrentStep().getName());
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        Validator.updateIdentifierSet(args[0]);
        Validator.updateComponentSet(args[0]);
    }

    private void checkArguments(String[] args){
        if (args.length != 1){
            throw new ValidationException("Invalid amount of arguments on DATABASE keyword.");
        }
    }
}
