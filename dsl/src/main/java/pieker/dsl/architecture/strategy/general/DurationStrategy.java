package pieker.dsl.architecture.strategy.general;

import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.exception.PiekerProcessingException;
import pieker.dsl.architecture.strategy.KeywordStrategy;

@Slf4j
public class DurationStrategy implements KeywordStrategy {
    @Override
    public void process(String[] args) {
        log.debug("processing DURATION node...");
        this.checkArguments(args);
        String duration = args[0];
        if (duration.contains(".")){
            Engine.getCurrentStep().setDuration(Float.parseFloat(duration));
        } else {
            Engine.getCurrentStep().setDuration(Integer.parseInt(duration));
        }
    }

    @Override
    public void validate(String[] args) {
        log.debug("validating DURATION node...");
        this.checkArguments(args);
    }

    private void checkArguments(String[] args){
        if (args.length != 1){
            throw new PiekerProcessingException("Invalid amount of arguments on DURATION keyword. Expected 1 but was: " + args.length + ".");
        }
    }
}
