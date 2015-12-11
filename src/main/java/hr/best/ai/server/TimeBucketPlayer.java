package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.exceptions.TimeLimitException;
import hr.best.ai.gl.AbstractPlayer;

import java.io.IOException;

/**
 * Player wrapper with limited time for signalNewState method. Exception is 
 * thrown if player crosses the time limit.
 */
public class TimeBucketPlayer extends AbstractPlayer {
	
    /**
     * TimeBucketPlayer delegates signalNewState to this player
     * while taking care of time constraints.
     */
    private final AbstractPlayer  player;
    
    /**
     * Number of milliseconds gained on each signalNewState()
     */
    private final long gainPerTurn;
    
    /**
     * Maximum number of milliseconds allowed to spend on one signalNewState()
     */
    private final long maxTime;
    
    /**
     * Current amount of milliseconds to spend.
     */
    private long currTimeBucket;

    /**
     * Wraps player.
     * 
     * @param gainPerTurn
     *            amount of milliseconds gained per turn
     * @param maxTime
     *            maximum amount of milliseconds available in a single turn
     */
    public TimeBucketPlayer(AbstractPlayer player, long gainPerTurn, long maxTime) {
        super(player.getName());
        this.player = player;
        this.gainPerTurn = gainPerTurn;
        this.maxTime = maxTime;
        this.currTimeBucket = maxTime;
    }

    @Override
    public void sendError(JsonObject message) {
        player.sendError(message);
    }

    /**
     * @throws TimeLimitException
     *             if player takes more time than he has in the "bucket".
     */
    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        long t0 = System.currentTimeMillis();
        currTimeBucket = Math.min(this.maxTime, currTimeBucket + gainPerTurn);
        state.add("timeGainPerTurn", new JsonPrimitive(gainPerTurn));
        state.add("timeLeftForMove", new JsonPrimitive(currTimeBucket));
        JsonObject sol = player.signalNewState(state);
        currTimeBucket -= System.currentTimeMillis() - t0;
        if (currTimeBucket < 0)
            throw new TimeLimitException();

        return sol;
    }

    @Override
    public void close() throws Exception {
        player.close();
    }
}
