package pieker.dsl.code.strategy.architecture;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.LinkProxy;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;

@Slf4j
public class LinkStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing LINK node...");
        this.checkArguments(args);

        LinkProxy linkProxy = new LinkProxy(args[0],args[1], args[2]);
        Engine.getCurrentStep().addStepComponent(linkProxy.getIdentifier(), linkProxy);
        log.debug("created link proxy: {} to {} in step '{}'", args[1],args[2], Engine.getCurrentStep().getName());
    }

    @Override
    public void validate(String[] args) {
       this.checkArguments(args);
       if (args[1].equals(args[2])){
           throw new ValidationException("A link cannot be established if source component equals target component.");
       }
       Validator.updateIdentifierSet(args[0]);
       Validator.updateComponentSet(args[1]+args[2]);
    }

    private void checkArguments(String[] args){
        if (args.length != 3){
            throw new ValidationException("Invalid amount of arguments on LINK keyword.");
        }
    }
}
