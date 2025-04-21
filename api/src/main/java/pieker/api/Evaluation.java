package pieker.api;

public interface Evaluation {

    String getAssertType();
    boolean isSuccess();
    String getErrorMessage();
    void evaluate(String arg);

}
