package hr.best.ai.games.sum;

import hr.best.ai.gl.Action;
import hr.best.ai.gl.IPlayer;
import hr.best.ai.gl.State;
import org.apache.log4j.Logger;

/**
 * Created by lpp on 9/22/15.
 */
public class SumDummyPlayer implements IPlayer {
    final static Logger logger = Logger.getLogger(SumDummyPlayer.class);
    private int inc;
    private String name;

    public SumDummyPlayer(int inc, String name) {
        this.inc = inc;
        this.name = name;
    }

    @Override
    public void sendError(String message) {
        logger.error("[" + name + "]: " + message);
    }

    @Override
    public Action signalNewState(State state) {
        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SumAction(inc);
    }

    @Override
    public void signalCompleted(String message) {
        logger.info("[" + name + "]: " + message);
    }

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public void close() throws Exception {
    }
}
