package hr.best.ai.server;

import hr.best.ai.gl.GameContext;
import hr.best.ai.gl.State;

public class ClientThreadDummy implements IClient {

    private final int id;

    public ClientThreadDummy(GameContext gc) {
        id = gc.registerPlayer(this);
    }

    @Override
    public void sendError(String message) {
        System.out.println("[" + id + "] error " + message);
    }

    @Override
    public void signalNewState(State state) {
        System.out.println("[" + id + "] new state:" + state.toJSONObject().toString());
    }

    @Override
    public void signalCompleted(String message) {
        System.out.println("[" + id + "] completed with message: " + message);
    }
}
