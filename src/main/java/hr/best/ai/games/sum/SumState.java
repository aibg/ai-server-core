package hr.best.ai.games.sum;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by lpp on 5/3/15.
 */
public class SumState implements State {
    private int sum = 0;
    final static Logger logger = Logger.getLogger(SumState.class);

    public SumState(int init) {
        sum = init;
    }

    @Override
    public boolean isFinal() {
        return sum > 10;
    }

    @Override
    public JsonObject toJSONObject() {
        JsonObject sol = new JsonObject();
        sol.addProperty("value", sum);
        return sol;
    }

    @Override
    public String toString() {
        logger.trace("This shouldn't be called. Use toJSONObject and then convert to string");
        return this.toJSONObject().toString();
    }

    @Override
    public State nextState(List<Action> actionList) {
        if (actionList.size() != 2)
            throw new IllegalArgumentException("Inavlid number of arguments! Expected 2. Got:" + actionList.size());

        for (Action a : actionList) {
            SumAction aa = (SumAction) a;
            sum += aa.getValue();
        }
        return new SumState(sum);
    }

    @Override
    public Action parseAction(JsonObject action) throws InvalidActionException {
        try {
            return new SumAction(action.get("value").getAsInt());
        } catch (RuntimeException ex) {
            throw new InvalidActionException(ex);
        }
    }

	@Override
	public JsonObject toJSONObjectAsPlayer(int playerId) {
		
		return toJSONObject();
	}

    @Override
    public int getWinner() {
        return 0;
    }
}
