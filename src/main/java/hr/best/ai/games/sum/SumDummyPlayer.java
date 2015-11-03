package hr.best.ai.games.sum;

import hr.best.ai.gl.AbstractPlayer;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by nmiculinic on 9/22/15.
 */
public class SumDummyPlayer extends AbstractPlayer {
    final static Logger logger = Logger.getLogger(SumDummyPlayer.class);
    private int inc;

    public SumDummyPlayer(int inc, String name) {
        super(name);
        this.inc = inc;
    }

    @Override
    public void sendError(JsonObject message) {
        logger.error("[" + this.getName() + "]: " + message);
    }

    @Override
    public JsonObject signalNewState(JsonObject state) {
        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JsonObject obj=new JsonObject();
        obj.add("value", new JsonPrimitive(inc));
        
        return obj;
    }

    @Override
    public void close() throws Exception {
    }
}
