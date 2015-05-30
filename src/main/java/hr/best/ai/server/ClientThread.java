package hr.best.ai.server;

import hr.best.ai.GameContext;
import hr.best.ai.gl.State;

/**
 * Created by lpp on 5/3/15.
 */
public class ClientThread {

    private final int id;

    public ClientThread(GameContext gc) {
        id = gc.registerPlayer(this);
    }

    public void sendError(String message) {
        System.out.println("[" + id + "] error " + message);
    }

    public void signalNewState(State state) {
        System.out.println("[" + id + "] new state:" + state.toJSONObject().toString());
    }

    public void signalCompleted(String message) {
        System.out.println("[" + id + "] completed with message: " + message);
    }
}
