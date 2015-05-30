package hr.best.ai;

import com.google.gson.JsonObject;
import hr.best.ai.gl.Action;
import hr.best.ai.gl.State;
import hr.best.ai.server.ClientThread;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

// Ovo treba zesci rewrite.... staviti jednu worker dretvu koja radi sve dok joj ostale salju svoje funkcije da
// izvrse u queue; kao u swingu invokeLater da ne bi bilo problema; previse toga moram onda stavljati volotile jbt.
// Tim pristupom cu rjesiti dobar dio potencijalnih problema. Covjece o cemu sve covijek mora razmisljati.

/**
 * Main class which surrounds whole play of one game. Game can be in one of three states, contained in enum GS. Those
 * are
 * INIT, PLAY and STOP. Initially game starts in INIT state and waits for clients to connect and register.
 * When someone calls {#startGame} method game context moves to PLAY state and cannot move back to initial state. In
 * it dedicated thread is periodically checking what's going on with the game, stopping it entirely if necessary(for
 * example players fail to respond under given time limit). From PLAY game state can move to STOP.
 * From PAUSE it's possible to come back to PLAY, however once STOP there's no coming back.
 */
public class GameContext {

    // Bookkeeping time in ms.
    private final static int UPDATE_TIME = 10;

    private volatile State state;

    public GameContext(State state, Supplier<IBucket> iBucketSupplier) {
        this.state = state;
        this.supplier = iBucketSupplier;
        clients = new ArrayList<>();
    }

    /**
     * Services for observers
     * LATER.. fuck the observers
     * */

    /**
     * Services for players
     */

    private final List<ClientThread> clients;
    private volatile int noPlayers = 0;
    private volatile int noCommitted = 0;
    private volatile Action[] actions;
    private volatile IBucket[] buckets;
    private final Supplier<IBucket> supplier;

    private GS gamestate = GS.INIT;

    public static enum GS {INIT, STOP, PLAY}

    /**
     * Register a player to game context. Returns player ID which is used in all subsequent calls to this Game Context.
     *
     * @param client
     * @return player ID
     */
    public synchronized int registerPlayer(ClientThread client) {
        clients.add(client);
        return noPlayers++;
    }

    public synchronized List<ClientThread> getPlayers() {
        return clients;
    }

    /**
     * Reports an error from clients to GameContext. Can be used if client disconnects or some other unknown error occurs.
     * @param playerID player ID which has caused the error.
     * @param e error in question.
     */
    public synchronized void signalError(int playerID, Exception e) {
        for (ClientThread cl : clients) {
            cl.sendError("Error happened: " + e.toString());
            cl.signalCompleted("ERROR");
        }
        stopGame();
    }

    public synchronized void commitAction(int playerID, Action action) {

        // Check that we are in valid state
        if (gamestate == GS.INIT) {
            clients.get(playerID).sendError("Game is not yet started!");
            return;
        }

        if (gamestate == GS.STOP) {
            clients.get(playerID).sendError("Game has ended!");
            return;
        }

        if (actions[playerID] != null) {
            clients.get(playerID).sendError("Action already commited");
            return;
        }

        bookkeeping();
        actions[playerID] = action;
        buckets[playerID].tok();

        noCommitted++;
        if (noCommitted == noPlayers) {
            iterate();
        }
    }

    /**
     * Runs one iteration of the game and signals all parties new game state.
     * It's only possible to run this if game is currently playing.
     */
    private synchronized void iterate() {
        if (noCommitted != noPlayers)
            throw new IllegalStateException("Not everyone has commited their action");

        if (gamestate != GS.PLAY)
            throw new IllegalStateException("Impossible to iterate if we're not playing");
        state = state.nextState(Arrays.asList(actions));

        for (ClientThread cl : clients) {
            cl.signalNewState(state);
            if (state.isFinal())
                cl.signalCompleted("Game Finished. We have a winner");
        }

        for (int i = 0; i < noPlayers; ++i) {
            actions[i] = null;
            buckets[i].tick();
        }

        noCommitted = 0;
    }

    /**
     * When game is started this performs necessary bookkeeping on players, that is checks that everybody responds
     * with given time limit and similar operations, if necessary STOPs the game and notifies the clients.
     */
    private synchronized void bookkeeping() {

        StringBuilder sb = new StringBuilder();
        boolean over = false;

        for (int i = 0; i < buckets.length; ++i) {
            if (!buckets[i].ok()) {
                sb.append("player [" + i + "] has overstepped its time limit");
                over = true;
            }
        }

        if (over) {
            gamestate = GS.STOP;
            for (ClientThread cl : clients)
                cl.sendError(sb.toString());
        }
    }

    /**
     * Services for control thread.
     */
    public synchronized void startGame() {
        if (gamestate != GS.INIT)
            throw new InvalidStateException("Game has already been started");

        actions = new Action[noPlayers];
        buckets = new IBucket[noPlayers];
        for (int i = 0; i < buckets.length; ++i)
            buckets[i] = supplier.get();

        for (ClientThread cl : clients) {
            cl.signalNewState(state);
            if (state.isFinal())
                cl.signalCompleted("Game Finished. We have a winner");
        }

        gamestate = GS.PLAY;

        new Thread(() -> {
            while (gamestate == GS.PLAY) {
                GameContext.this.bookkeeping();
                try {
                    Thread.sleep(UPDATE_TIME);
                } catch (InterruptedException ignorable) {
                }
            }
        }).start();

    }

    /**
     * Stops the game which has started. This function is idempotent, that is multiples calles does nothing.
     *
     * @throws sun.plugin.dom.exception.InvalidStateException if game hasn't yet started
     */
    public synchronized void stopGame() {
        switch (gamestate) {
            case INIT:
                throw new InvalidStateException("Game has not yet started");
            case PLAY:
            case STOP:
                gamestate = GS.STOP;
        }
    }

    /**
     * Returns current game state. By game state we mean is it playing, pausing, stopping, or waiting for all players to join.
     *
     * @return one of STOP, PAUSE, INIT, PLAY
     */
    public GS getGamestate() {
        return this.gamestate;
    }

    public synchronized JsonObject status() {
        JsonObject sol = new JsonObject();
        sol.addProperty("gameState", gamestate.toString());
        sol.add("current State", state.toJSONObject());
        return sol;
    }
}
