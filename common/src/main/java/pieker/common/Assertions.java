package pieker.common;

import java.util.List;

public interface Assertions {

    void evaluate(String[] args);
    List<Evaluation> getEvaluation();
    String getIdentifier();

}
