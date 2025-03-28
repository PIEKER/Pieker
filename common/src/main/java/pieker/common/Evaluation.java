package pieker.common;

public interface Evaluation {

    String getAssertType();
    boolean isSuccess();
    String getErrorMessage();
    void evaluate(String arg);

}
