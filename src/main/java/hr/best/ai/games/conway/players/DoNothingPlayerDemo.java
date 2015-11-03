package hr.best.ai.games.conway.players;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.AbstractPlayer;

import java.io.IOException;

/**
 * Created by nmiculinic on 10/28/15.
 */
public class DoNothingPlayerDemo extends AbstractPlayer {

    public DoNothingPlayerDemo(String name) {
        super(name);
    }

    @Override
    public void sendError(JsonObject message) {
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
    public void close() throws Exception {
    }
}
