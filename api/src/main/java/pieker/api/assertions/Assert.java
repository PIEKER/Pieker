package pieker.api.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.api.assertions.util.Util;
import pieker.api.Assertions;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public abstract class Assert implements Assertions {

    protected final String assertPlugin;
    protected String identifier;
    protected int assertAfter = 0;

    protected final List<Bool> boolList = new ArrayList<>();
    protected final List<Equals> equalsList = new ArrayList<>();
    protected final List<Null> nullList = new ArrayList<>();

    protected Assert(String assertPlugin, String identifier){
        this.assertPlugin = assertPlugin;
        this.identifier = identifier;
    }

    protected Assert(String assertPlugin){
        this(assertPlugin, "");
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