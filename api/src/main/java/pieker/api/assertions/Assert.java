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

    public void addBoolAssertion(String boolType, String expression, String value){
        this.boolList.add(new Bool(Util.parseBoolean(boolType), expression, value));
    }

    public void addEqualsAssertion(String isEqual,String expected, String value){
        this.equalsList.add(new Equals(Util.parseBoolean(isEqual), expected, value));
    }

    public void addNullAssertion(String isNull, String value){
        this.nullList.add(new Null(Util.parseBoolean(isNull), value));
    }

    public abstract void validate();
    public abstract void processAssert();
    protected abstract void evaluateBoolNode(Bool bool, String[] args);
    protected abstract void evaluateEqualsNode(Equals equals, String[] args);
    protected abstract void evaluateNullNode(Null nuLL, String[] args);
}