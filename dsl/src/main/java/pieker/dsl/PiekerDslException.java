package pieker.dsl;

public class PiekerDslException extends RuntimeException {

    public PiekerDslException(String message) {
        super(message);
    }

    public PiekerDslException(String message, Throwable throwable){
        super(message,throwable);
    }
}
