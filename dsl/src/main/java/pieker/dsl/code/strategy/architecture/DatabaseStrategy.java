package pieker.dsl.code.strategy.architecture;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.DatabaseProxy;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;

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
