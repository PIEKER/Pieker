package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Evaluation;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;
import pieker.api.exception.ValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class LogAssert extends Assert {

    private static final String ASSERT_PLUGIN = "Log";
    private List<String> logLines = new LinkedList<>();

    public LogAssert(){
        super(ASSERT_PLUGIN);
    }

    public LogAssert(String identifier) {
        super(ASSERT_PLUGIN, identifier);
    }

    @Override
    public void validate(int line) {
        log.debug("validate LogEvaluation");
        this.boolList.forEach(bool -> {
            String[] valueLine = bool.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException("invalid valueLine detected: " + bool.getValue());
            }
            bool.validate(line);
            this.validateKeyword(valueLine);
        });
        this.equalsList.forEach(equals -> {
            String[] valueLine = equals.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException("invalid valueLine detected: " + equals.getValue());
            }
           this.validateKeyword(valueLine);
        });
        this.nullList.forEach(nullNode -> {
            String[] valueLine = nullNode.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException("invalid valueLine detected: " + nullNode.getValue());
            }
            this.validateKeyword(valueLine);
        });
    }

    @Override
    public void processAssert() {
        log.info("process LogEvaluation");
    }

    @Override
    protected void evaluateBoolNode(Bool bool) {
        String[] valueLine = bool.getValue().split(" ");
        if (valueLine.length < 1) {
            bool.setErrorMessage("invalid valueLine detected: " + bool.getValue());
            return;
        }
        Keyword key = Keyword.valueOf(valueLine[0].substring(1).toUpperCase());
        key.processValue(
                bool,
                this.formatArgsToCharakterKeys(Arrays.copyOfRange(valueLine, 1, valueLine.length)),
                this.logLines.toArray(new String[0])
        );
    }

    @Override
    protected void evaluateEqualsNode(Equals equals) {
        log.debug("evaluate Equals in LogEvaluation");
        String[] valueLine = equals.getValue().split(" ");
        if (valueLine.length < 1) {
            equals.setErrorMessage("invalid valueLine detected: " + equals.getValue());
            return;
        }
        Keyword key = Keyword.valueOf(valueLine[0].substring(1).toUpperCase());
        key.processValue(
                equals,
                this.formatArgsToCharakterKeys(Arrays.copyOfRange(valueLine, 1, valueLine.length)),
                this.logLines.toArray(new String[0])
        );
    }

    @Override
    protected void evaluateNullNode(Null nuLL) {
        log.debug("evaluate Null in LogEvaluation");
        String[] valueLine = nuLL.getValue().split(" ");
        if (valueLine.length < 1) {
            nuLL.setErrorMessage("invalid valueLine detected: " + nuLL.getValue());
            return;
        }
        Keyword key = Keyword.valueOf(valueLine[0].substring(1).toUpperCase());
        key.processValue(
                nuLL,
                this.formatArgsToCharakterKeys(Arrays.copyOfRange(valueLine, 1, valueLine.length)),
                this.logLines.toArray(new String[0])
        );
    }

    @Override
    public void evaluate() {
        log.debug("evaluate LogEvaluation");
        String dir = System.getProperty("assertDirectory");
        if (dir == null) {
            log.error("System property 'assertDirectory' not set.");
            this.invalidateAssert("System property 'assertDirectory' not set.");
            return;
        }
        Path filePath =  Paths.get(dir, this.identifier +".log"); //FIXME
        if (!Files.exists(filePath)) {
            log.error("File does not exist: {}", filePath);
            this.invalidateAssert("File does not exist: " + filePath);
            return;
        }
        try {
            logLines = Files.readAllLines(filePath);
        } catch (IOException e) {
            log.error("Error reading the file: {}", e.getMessage());
        }

        this.boolList.forEach(this::evaluateBoolNode);
        this.equalsList.forEach(this::evaluateEqualsNode);
        this.nullList.forEach(this::evaluateNullNode);
    }

    @Override
    public List<Evaluation> getEvaluation() {
        return List.of();
    }

    @Override
    public boolean requiresConnectionParam(){
        return false;
    }

    @Override
    public void setupConnectionParam(JSONObject cpJson) {
        log.info("setup ConnectionParam in LogEvaluation");
    }

    private void validateKeyword(String[] valueLine){
        try{
            Keyword key = Keyword.valueOf(valueLine[0].substring(1).toUpperCase());
            key.validate(this.formatArgsToCharakterKeys(Arrays.copyOfRange(valueLine, 1, valueLine.length)));
        } catch (IllegalArgumentException _){
            throw new ValidationException("invalid keyword detected: " + valueLine[0]);
        }
    }

    private String[] formatArgsToCharakterKeys(String[] args){
        List<String> characterKeys = new LinkedList<>();
        for (String arg : args) {
            try {
                characterKeys.add(arg.substring(1).toUpperCase());
            } catch (IllegalArgumentException _) {
                log.warn("Invalid argument {} for keyword exists.", arg);
            }
        }
        return characterKeys.toArray(new String[0]);
    }
}
