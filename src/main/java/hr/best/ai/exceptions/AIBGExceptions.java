package hr.best.ai.exceptions;

/**
 * Created by lpp on 10/26/15.
 */
public class AIBGExceptions extends RuntimeException {
    public AIBGExceptions(String error) {
        super(error);
    }

    public AIBGExceptions() {
        super();
    }
}
