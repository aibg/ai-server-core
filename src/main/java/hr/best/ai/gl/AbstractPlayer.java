package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;

import java.io.IOException;

import com.google.gson.JsonObject;

/**
 * Most generalized player class. Players are added to GameContext which 
 * manages their communication with the game. They could be for example 
 * local processes, communicate with the game through TCP protocol or 
 * something else.
 */
public abstract class AbstractPlayer implements AutoCloseable {

    private final String name;

    public AbstractPlayer(String name) {
        this.name = name;
    }
    /**
     * Sends error message to player 
     * @param message the message
     */
    public abstract void sendError(JsonObject message);

    /**
     * Retrieves player's action.
     * 
     * @param state
     *            JsonObject sent to player
     * @return JsonObject containing player action
     * @throws IOException in case of communication error
     * @throws InvalidActionException in case JsonObject can't be created from player's output
     */
    public abstract JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException;

    /**
     * @return player name
     */
    public String getName() {
        return this.name;
    }
}
