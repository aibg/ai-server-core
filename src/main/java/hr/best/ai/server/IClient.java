package hr.best.ai.server;

import hr.best.ai.gl.State;

/**
 * Created by lpp on 6/26/15.
 */
public interface IClient {
    void sendError(String message);

    void signalNewState(State state);

    void signalCompleted(String message);
}
