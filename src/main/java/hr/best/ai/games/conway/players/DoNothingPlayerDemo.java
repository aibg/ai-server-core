package hr.best.ai.games.conway.players;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.AbstractPlayer;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by nmiculinic on 10/28/15.
 */
public class DoNothingPlayerDemo extends AbstractPlayer {

    final static Logger logger = Logger.getLogger(DoNothingPlayerDemo.class);
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
        logger.debug(String.format(
                "%s recieved: \"%s\""
                , this.getName()
                , sol.toString()
        ));
        return sol;
    }

    @Override
    public void close() throws Exception {
    }
}
