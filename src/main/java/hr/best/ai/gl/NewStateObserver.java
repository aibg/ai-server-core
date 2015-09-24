package hr.best.ai.gl;

public interface NewStateObserver extends AutoCloseable {
    public void signalNewState(State state);
    public void signalCompleted(String message);
    public String getName();
}
