package pieker.log.eval;

import lombok.extern.slf4j.Slf4j;
import pieker.api.Assertion;
import pieker.api.Evaluation;
import pieker.api.assertions.Assert;
import pieker.api.assertions.Bool;
import pieker.api.assertions.Equals;
import pieker.api.assertions.Null;
import pieker.api.exception.ValidationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class LogAssert extends Assert {

    private static final String ASSERT_PLUGIN = "Log";
    private List<String> logLines = new LinkedList<>();
    private long finishedAt = 0L;

    private static final String VALIDATION_ERROR_PREFIX = "invalid valueLine detected: ";
    private static final String FILE_SUFFIX = ".log";

    public LogAssert(){
        super(ASSERT_PLUGIN);
    }

    public LogAssert(String identifier) {
        super(ASSERT_PLUGIN, identifier);
    }

    private LogAssert(LogAssert logAssert){
        super(logAssert);
        this.logLines = logAssert.logLines;
        this.finishedAt = logAssert.finishedAt;
    }

    @Override
    public void validate(int line) {
        log.debug("validate LogEvaluation");
        this.boolList.forEach(bool -> {
            String[] valueLine = bool.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException(VALIDATION_ERROR_PREFIX + bool.getValue());
            }
            bool.validate(line);
            this.validateKeyword(valueLine);
        });
        this.equalsList.forEach(equals -> {
            String[] valueLine = equals.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException(VALIDATION_ERROR_PREFIX + equals.getValue());
            }
           this.validateKeyword(valueLine);
        });
        this.nullList.forEach(nullNode -> {
            String[] valueLine = nullNode.getValue().split(" ");
            if (valueLine.length < 1 || !valueLine[0].startsWith("@")) {
                throw new ValidationException(VALIDATION_ERROR_PREFIX + nullNode.getValue());
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
            bool.setErrorMessage(VALIDATION_ERROR_PREFIX + bool.getValue());
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
            equals.setErrorMessage(VALIDATION_ERROR_PREFIX + equals.getValue());
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
            nuLL.setErrorMessage(VALIDATION_ERROR_PREFIX + nuLL.getValue());
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
        String filename = this.stepId + "_" + this.identifier.replace("-", "_") + FILE_SUFFIX;
        String filenameBackup = this.stepId + "_PIEKER_PROXY_" + this.identifier.replace("-", "_") + FILE_SUFFIX;
        File file = this.getFileMap().get(FILE_SUFFIX).get(filename);
        if (file == null || !file.isFile()) {
            file = this.getFileMap().get(FILE_SUFFIX).get(filenameBackup);
            if (file == null || !file.isFile()) {
                log.error("No file found for name {}", filename);
                this.invalidateAssert("File does not match file: " + filename);
                return;
            }
        }
        try {
            this.logLines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            log.error("Error reading the file: {}", e.getMessage());
            this.invalidateAssert("Error reading the file: " + e.getMessage());
            return;
        }

        this.boolList.forEach(this::evaluateBoolNode);
        this.equalsList.forEach(this::evaluateEqualsNode);
        this.nullList.forEach(this::evaluateNullNode);
    }

    @Override
    public List<Evaluation> getEvaluation() {
        List<Evaluation> evaluationList = new ArrayList<>();
        evaluationList.addAll(this.boolList);
        evaluationList.addAll(this.equalsList);
        evaluationList.addAll(this.nullList);
        return evaluationList;
    }

    @Override
    public Assertion copy() {
        return new LogAssert(this);
    }

    @Override
    public boolean requiresConnectionParam(){
        return false;
    }

    @Override
    public void setConnectionParam(String gatewayUrl) {
        log.info("setup ConnectionParam in LogEvaluation");
    }

    @Override
    public void prepare() {
        this.finishedAt = System.currentTimeMillis();
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
