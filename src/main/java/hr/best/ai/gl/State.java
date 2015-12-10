package hr.best.ai.gl;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;

import java.util.List;

/**
 * Game is composed of states. Every next state is computed from last one with
 * player actions as input.
 */
public interface State {
	/**
	 * Checks if game is finished (final state reached)
	 * 
	 * @return <code>true</code> if state is final
	 */
    public boolean isFinal();

    /**
     * @return state in json form
     */
    public JsonObject toJSONObject();

    /**
     * Returns state as a personalized json object for player with id playerID
     * 
     * @param playerId
     *            player position in player list of game context TODO
     * @return state as json object
     */
    public JsonObject toJSONObjectAsPlayer(int playerId);
    
    /**
     * Returns next state calculated with player actions.
     * 
     * @param actionList
     *            player actions (actions of player with id playerID are
     *            actionList.get(playerID))TODO
     * @return next state
     */
    public State nextState(List<Action> actionList);

    /**
     * Parses player action from json.
     * 
     * @param action
     *            player action in json form
     * @return action
     * @throws InvalidActionException
     *             if action cannot be parsed properly
     */
    Action parseAction(JsonObject action) throws InvalidActionException;

    /**
     * @return -1 for TIE, else playerID for winner
     */
    public int getWinner();
}
