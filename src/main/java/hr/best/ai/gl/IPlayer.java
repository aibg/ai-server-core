package hr.best.ai.gl;

public interface IPlayer extends AutoCloseable {

    public void sendError(String message);

    public Action signalNewState(State state);

    public void signalCompleted(String message);
}
