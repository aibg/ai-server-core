package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;

import java.io.IOException;

public interface IPlayer extends AutoCloseable {

    public void sendError(String message);

    public Action signalNewState(State state) throws IOException, InvalidActionException;

    public void signalCompleted(String message);
}
