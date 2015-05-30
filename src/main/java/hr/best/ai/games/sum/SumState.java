package hr.best.ai.games.sum;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;

import java.util.List;

/**
 * Created by lpp on 5/3/15.
 */
public class SumState implements State {
    private int sum = 0;

    public SumState(int init) {
        sum = init;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public JsonObject toJSONObject() {
        JsonObject sol = new JsonObject();
        sol.addProperty("value", sum);
        return sol;
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
}
