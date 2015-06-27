package hr.best.ai.server;

import hr.best.ai.gl.State;

public interface IClient {

    public void sendError(String message);

    public void signalNewState(State state);

    public void signalCompleted(String message);
}
