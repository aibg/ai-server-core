package hr.best.ai.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.exceptions.TimeLimitException;
import hr.best.ai.gl.AbstractPlayer;

import java.io.IOException;

public class TimeBucketPlayer extends AbstractPlayer {
    private final AbstractPlayer  player;
    private final long gainPerTurn;
    private final long maxTime;
    private long currTimeBucket;

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
