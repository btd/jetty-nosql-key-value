package org.eclipse.jetty.nosql.key_value.session;

public class SerializationException extends RuntimeException {
    private static final long serialVersionUID = 7772013267017548308L;

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
