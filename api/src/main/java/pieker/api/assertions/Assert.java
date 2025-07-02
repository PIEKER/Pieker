package pieker.api.assertions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pieker.api.assertions.util.Util;
import pieker.api.Assertion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public abstract class Assert implements Assertion {

    @JsonIgnore
    protected final String assertPlugin;
    @JsonIgnore
    protected String stepId;

    protected String identifier;

    protected boolean skip;

    @JsonIgnore
    private boolean requiresComponent = false;
    @JsonIgnore
    private String requiredComponent;

    @JsonIgnore
    private Map<String, Map<String, File>> fileMap = new HashMap<>();

    @JsonIgnore
    protected final List<Bool> boolList = new ArrayList<>();
    @JsonIgnore
    protected final List<Equals> equalsList = new ArrayList<>();
    @JsonIgnore
    protected final List<Null> nullList = new ArrayList<>();

    protected Assert(String assertPlugin, String identifier){
        this.assertPlugin = assertPlugin;
        this.identifier = identifier;
    }

    protected Assert(String assertPlugin){
        this(assertPlugin, "");
    }

    protected Assert(Assert ass){
        this.assertPlugin = ass.assertPlugin;
        this.stepId = ass.stepId;
        this.identifier = ass.identifier;
        this.skip = ass.skip;
        this.requiresComponent = ass.requiresComponent;
        this.requiredComponent = ass.requiredComponent;
        this.fileMap = ass.getFileMap();
        this.boolList.addAll(ass.boolList.stream().map(b -> (Bool) b.copy()).toList());
        this.equalsList.addAll(ass.equalsList.stream().map(e -> (Equals) e.copy()).toList());
        this.nullList.addAll(ass.nullList.stream().map(n -> (Null) n.copy()).toList());
    }
    /**
     * Adds BoolAssertion storing DSL information.
     *
     * @param boolType true | false
     * @param expression JavaScript expression
     * @param value data concatenated with expression
     */
    public void addBoolAssertion(String boolType, String expression, String value){
        this.boolList.add(new Bool(Util.parseBoolean(boolType), expression, value));
    }

    /**
     * Adds EqualsAssertion storing DSL information.
     *
     * @param isEqual true | false as String
     * @param expected String
     * @param value String
     */
    public void addEqualsAssertion(String isEqual,String expected, String value){
        this.equalsList.add(new Equals(Util.parseBoolean(isEqual), expected, value));
    }

    /**
     * Adds EqualsAssertion storing DSL information.
     *
     * @param isNull true | false as String
     * @param value String
     */
    public void addNullAssertion(String isNull, String value){
        this.nullList.add(new Null(Util.parseBoolean(isNull), value));
    }

    /**
     * Marks all Assertions as invalid, due to global error.
     * @param message describing error
     */
    public void invalidateAssert(String message){
        this.boolList.forEach(bool -> bool.setErrorMessage(message));
        this.equalsList.forEach(equals -> equals.setErrorMessage(message));
        this.nullList.forEach(null1 -> null1.setErrorMessage(message));
    }

    /**
     * Sets a component identifier and enables a requiredComponent flag to reboot component for evaluation.
     * @param requiredComponent string
     */
    public void setRequiredComponent(String requiredComponent){
        this.requiredComponent = requiredComponent;
        this.requiresComponent = !this.requiredComponent.isEmpty();
    }

    /**
     * Allows further syntax validation, next to Parsing Rules.
     */
    public abstract void validate(int line);

    /**
     * Allows data processing before any performed evaluation.
     */
    public abstract void processAssert();

    /**
     * Implements required evaluation of assertBool
     * @param bool instance storing DSL information
     */
    protected abstract void evaluateBoolNode(Bool bool);

    /**
     * Implements required evaluation of assertEquals
     * @param equals instance storing DSL information
     */
    protected abstract void evaluateEqualsNode(Equals equals);

    /**
     * Implements required evaluation of assertNull
     * @param nuLL instance storing DSL information
     */
    protected abstract void evaluateNullNode(Null nuLL);
}