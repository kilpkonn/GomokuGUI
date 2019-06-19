package ee.taltech.gomoku.app.exceptions;

public class ThinkingTimeoutException extends Exception {

    public ThinkingTimeoutException(String message) {
        super(message);
    }
}
