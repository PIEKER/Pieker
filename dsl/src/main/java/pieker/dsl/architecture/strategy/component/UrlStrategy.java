package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.component.ServiceProxy;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.util.Util;

import java.util.Arrays;

@Slf4j
public class UrlStrategy implements KeywordStrategy {

    @Override
    public void process(String[] args) {
        log.debug("processing URL node...");
        this.checkArguments(args);

        ServiceProxy serviceProxy = new ServiceProxy(args[0], args[1]);
        serviceProxy.setUrlList(Arrays.stream(Util.convertStringToStringArray(args[2])).toList());
        Engine.getCurrentStep().addStepComponent(serviceProxy.getIdentifier(), serviceProxy);

        log.debug("added service-proxy with url restriction for service '{}' in step '{}'",
                args[0], Engine.getCurrentStep().getName());

    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        Validator.updateIdentifierSet(args[0]);
        Validator.updateComponentSet(args[0]);
    }

    private void checkArguments(String[] args){
        if (args.length != 3){
            throw new ValidationException("Invalid amount of arguments on URL keyword.");
        }
    }
}
