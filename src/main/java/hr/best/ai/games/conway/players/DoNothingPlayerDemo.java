package hr.best.ai.games.conway.players;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.IPlayer;

import java.io.IOException;

/**
 * Created by lpp on 10/28/15.
 */
public class DoNothingPlayerDemo implements IPlayer {
    @Override
    public void sendError(String message) {
        System.err.println(message);
    }

    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        JsonObject sol = new JsonObject();
        sol.add("cells", new JsonArray());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sol;
    }

    @Override
    public void signalCompleted(String message) {
    }

    @Override
    public String getName() {
        return "Chillin' on the beach.";
    }

    @Override
    public void close() throws Exception {
    }
}
