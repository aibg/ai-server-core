package hr.best.ai.exceptions;

/**
 * Created by lpp on 5/3/15.
 */
public class InvalidActionException extends Exception {
    public InvalidActionException(Exception ex) {
        super(ex);
    }
}