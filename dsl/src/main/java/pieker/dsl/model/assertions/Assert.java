package pieker.dsl.model.assertions;

import lombok.Getter;
import lombok.Setter;
import pieker.common.Assertions;
import pieker.dsl.util.Util;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public abstract class Assert implements Assertions {

    protected final String identifier;

    protected final List<Bool> boolList = new ArrayList<>();
    protected final List<Equals> equalsList = new ArrayList<>();
    protected final List<Null> nullList = new ArrayList<>();

    protected Assert(String identifier){
        this.identifier = identifier;
    }

    public void addBoolAssertion(String boolType, String expression, String value){
        this.boolList.add(new Bool(Util.parseBoolean(boolType), expression, value));
    }

    public void addEqualsAssertion(String expected, String value){
        this.equalsList.add(new Equals(expected, value));
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