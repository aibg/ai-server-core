package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;

import java.io.IOException;

import com.google.gson.JsonObject;

public interface IPlayer extends AutoCloseable {

    public void sendError(String message);

    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException;

    public void signalCompleted(String message);

    public String getName();

	public IBucket getBucket();
}
