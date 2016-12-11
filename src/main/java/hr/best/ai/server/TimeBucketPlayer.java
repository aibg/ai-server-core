package hr.best.ai.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hr.best.ai.exceptions.TimeLimitException;
import hr.best.ai.gl.AbstractPlayer;

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
    
    private final long millisecondsGainedPerTurn;
    
    private final long maxTurnTime;
    
    private long currentTurnTimeLeft;

    public TimeBucketPlayer(AbstractPlayer player, long millisecondsGainedPerTurn, long maxTurnTime) {
        super(player.getName());
        this.player = player;
        this.millisecondsGainedPerTurn = millisecondsGainedPerTurn;
        this.maxTurnTime = maxTurnTime;
        this.currentTurnTimeLeft = maxTurnTime;
    }

    @Override
    public void sendError(JsonObject message) {
        player.sendError(message);
    }

    @Override
    public JsonElement signalNewState(JsonObject state) throws Exception {
        long t0 = System.currentTimeMillis();
        currentTurnTimeLeft = Math.min(this.maxTurnTime, currentTurnTimeLeft + millisecondsGainedPerTurn);
        state.add("timeGainPerTurn", new JsonPrimitive(millisecondsGainedPerTurn));
        state.add("timeLeftForMove", new JsonPrimitive(currentTurnTimeLeft));
        JsonElement sol = player.signalNewState(state);
        currentTurnTimeLeft -= System.currentTimeMillis() - t0;
        if (currentTurnTimeLeft < 0)
            throw new TimeLimitException();

        return sol;
    }

    @Override
    public void signalFinal(JsonObject state) throws Exception {
        player.signalFinal(state);
        close();
    }

    @Override
    public void close() throws Exception {
        player.close();
    }
}
