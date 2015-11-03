package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface IPlayer extends AutoCloseable {

    public void sendError(JsonObject message);

    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException;

    public String getName();
}
