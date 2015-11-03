package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;

import java.io.IOException;

import com.google.gson.JsonObject;

public abstract class AbstractPlayer implements AutoCloseable {

    private final String name;

    public AbstractPlayer(String name) {
        this.name = name;
    }

    public abstract void sendError(JsonObject message);

    public abstract JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException;

    public String getName() {
        return this.name;
    }
}
