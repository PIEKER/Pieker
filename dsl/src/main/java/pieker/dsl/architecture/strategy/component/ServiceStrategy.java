package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.template.component.ServiceProxy;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.util.Util;

@Slf4j
public class ServiceStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing SERVICE node...");
        this.checkArguments(args);
        String[] identifiers = Util.convertStringToStringArray(args[0]);
        for (String identifier : identifiers){
            ServiceProxy serviceProxy = new ServiceProxy(identifier);
            Engine.getCurrentStep().addStepComponent(serviceProxy.getIdentifier(), serviceProxy);
            log.debug("added service-proxy for service '{}' in step '{}'", identifier, Engine.getCurrentStep().getName());
        }
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        String[] identifiers = Util.convertStringToStringArray(args[0]);
        for (String identifier : identifiers){
            Validator.updateIdentifierSet(identifier);
            Validator.updateComponentSet(identifier);
        }

    }

    private void checkArguments(String[] args){
        if (args.length != 1){
            throw new ValidationException("Invalid amount of arguments on SERVICE keyword.");
        }
    }
}
