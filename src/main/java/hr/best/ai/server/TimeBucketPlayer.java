package hr.best.ai.server;

import com.google.gson.JsonObject;
import hr.best.ai.exceptions.InvalidActionException;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.bucket.IBucket;

import java.io.IOException;

public class TimeBucketPlayer extends AbstractPlayer {
    private final AbstractPlayer  player;
    private final IBucket bucket;

    public TimeBucketPlayer(AbstractPlayer player, IBucket bucket) {
        super(player.getName());
        this.player = player;
        this.bucket = bucket;
    }

    @Override
    public void sendError(JsonObject message) {
        player.sendError(message);
    }

    @Override
    public JsonObject signalNewState(JsonObject state) throws IOException, InvalidActionException {
        long t0 = System.currentTimeMillis();
        bucket.fill();
        JsonObject sol = player.signalNewState(state);
        bucket.take(System.currentTimeMillis() - t0);
        return sol;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    public IBucket getBucket() {
        return bucket;
    }

    @Override
    public void close() throws Exception {
        player.close();
    }
}
