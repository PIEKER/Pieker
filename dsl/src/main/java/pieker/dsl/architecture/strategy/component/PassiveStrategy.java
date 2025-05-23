package pieker.dsl.architecture.strategy.component;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.component.Traffic;
import pieker.dsl.architecture.exception.ValidationException;
import pieker.dsl.architecture.preprocessor.Validator;
import pieker.dsl.architecture.strategy.KeywordStrategy;
import pieker.dsl.architecture.template.traffic.Request;
import pieker.dsl.architecture.template.traffic.Sql;
import pieker.dsl.Keyword;

@Slf4j
public class PassiveStrategy implements KeywordStrategy {

    @Override
    public void process(String[] args) {
        log.debug("processing PASSIVE node...");
        this.checkArguments(args);

        String[] passiveKey = args[0].substring(1).split(" ");
        assert passiveKey.length == 2;

        switch (getKeywordAfterPassive(args)){
            case REQUEST -> {
                String parameter = args[2].trim();
                Traffic traffic = new Traffic(passiveKey[1], new Request(args[1], parameter));
                Engine.getCurrentStep().addStepComponent(traffic.getIdentifier(), traffic);
            }
            case SQL -> {
                String parameter = args[3].trim();
                Traffic traffic = new Traffic(passiveKey[1], new Sql(args[1], args[2], parameter));
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
        if (args.length < 3){
            throw new ValidationException("Invalid amount of arguments on PASSIVE keyword.");
        }

        switch (getKeywordAfterPassive(args)){
            case REQUEST -> {
                if (args.length != 3) throw new ValidationException("Invalid amount of arguments on REQUEST after PASSIVE keyword.");
            }
            case SQL -> {
                if (args.length != 4) throw new ValidationException("Invalid amount of arguments on SQL after PASSIVE keyword.");
            }
            default -> {
                throw new ValidationException("Invalid keyword after passive detected. Expected: REQUEST/SQL.");
            }
        }
    }

    private Keyword getKeywordAfterPassive(String[] args){
        String[] passiveKey = args[0].substring(1).split(" ");
        assert passiveKey.length == 2;

        return Keyword.valueOf(passiveKey[0].toUpperCase());
    }

}
