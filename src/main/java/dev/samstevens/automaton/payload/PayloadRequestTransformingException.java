package dev.samstevens.automaton.payload;

public class PayloadRequestTransformingException extends Exception {

    public PayloadRequestTransformingException(String message) {
        super(message);
    }

    public PayloadRequestTransformingException(Throwable cause) {
        super(cause);
    }
}
