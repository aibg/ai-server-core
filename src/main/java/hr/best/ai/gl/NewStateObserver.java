package hr.best.ai.gl;

import com.google.gson.JsonObject;

public interface NewStateObserver extends AutoCloseable {
    public void signalNewState(State state);
    public void signalError(JsonObject message);
    public String getName();
}
