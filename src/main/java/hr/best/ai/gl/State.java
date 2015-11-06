package hr.best.ai.gl;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;

import java.util.List;

/**
 * Created by lpp on 5/3/15.
 */
public interface State {
	
    public boolean isFinal();

    public JsonObject toJSONObject();

    public JsonObject toJSONObjectAsPlayer(int playerId);
    
    public State nextState(List<Action> actionList);

    Action parseAction(JsonObject action) throws InvalidActionException;

    /**
     *
     * @return -1 for TIE, else playerID for winner
     */
    public int getWinner();
}
