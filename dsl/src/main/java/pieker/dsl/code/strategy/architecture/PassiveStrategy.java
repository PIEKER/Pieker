package pieker.dsl.code.strategy.architecture;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.Engine;
import pieker.dsl.code.component.Traffic;
import pieker.dsl.code.exception.ValidationException;
import pieker.dsl.code.preprocessor.Validator;
import pieker.dsl.code.strategy.KeywordStrategy;
import pieker.dsl.code.template.architecture.Request;
import pieker.dsl.code.template.architecture.Sql;
import pieker.dsl.Keyword;

@Slf4j
public class PassiveStrategy implements KeywordStrategy {

    @Override
    public void process(String[] args) {
        log.debug("processing PASSIVE node...");
        this.checkArguments(args);

        String[] passiveKey = args[0].substring(1).split(" ");
        assert passiveKey.length == 2;
        String parameter = args[2].trim();

        switch (Keyword.valueOf(passiveKey[0].toUpperCase())){
            case REQUEST -> {
                Traffic traffic = new Traffic(passiveKey[1], new Request(args[1], parameter));
                Engine.getCurrentStep().addStepComponent(traffic.getIdentifier(), traffic);
            }
            case SQL -> {
                Traffic traffic = new Traffic(passiveKey[1], new Sql(args[1], parameter));
                Engine.getCurrentStep().addStepComponent(traffic.getIdentifier(), traffic);
            }
            default -> log.warn("unknown follow-up keyword on PASSIVE node detected: '{}'. Skipping condition.", passiveKey[0]);
        }
        log.debug("created traffic test-component in step '{}'.", Engine.getCurrentStep().getName());
    }

    @Override
    public void validate(String[] args) {
        this.checkArguments(args);
        String[] passiveKey = args[0].split(" ");
        assert passiveKey.length == 2;

        Validator.updateIdentifierSet(passiveKey[1]);
    }

    private void checkArguments(String[] args){
        if (args.length != 3){
            throw new ValidationException("Invalid amount of arguments on PASSIVE keyword.");
        }
    }

}
