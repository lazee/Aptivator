package net.jakobnielsen.aptivator;

public class AptivatorException extends Exception {

    public AptivatorException(String message) {
        super(message);
    }

    public AptivatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AptivatorException(Throwable cause) {
        super(cause);
    }
}
