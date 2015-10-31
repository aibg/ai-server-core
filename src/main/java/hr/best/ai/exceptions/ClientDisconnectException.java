package hr.best.ai.exceptions;

/**
 * Exception arising when client disconnects or some other error occurs with TCP connection.
 */
public class ClientDisconnectException extends AIBGExceptions {
    public ClientDisconnectException(String error) {
        super(error);
    }
}