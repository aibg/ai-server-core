package hr.best.ai.gl;

import hr.best.ai.exceptions.InvalidActionException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main class which surrounds whole play of one game. Game can be in one of three states, contained in enum GS. Those
 * are
 * INIT, PLAY and STOP. Initially game starts in INIT state and waits for players to connect and register.
 * When someone calls {#startGame} method game context moves to PLAY state and cannot move back to initial state. In
 * it dedicated thread is periodically checking what's going on with the game, stopping it entirely if necessary(for
 * example players fail to respond under given time limit). From PLAY game state can move to STOP.
 * From PAUSE it's possible to come back to PLAY, however once STOP there's no coming back.
 */
public class GameContext implements AutoCloseable {

    final static Logger logger = Logger.getLogger(GameContext.class);
    private final List<IPlayer> players;
    private final ExecutorService threadPool;
    private final int maxPlayers;
    /**
     * Services for observers
     * LATER.. fuck the observers
     */
    private State state;
    private GS gamestate = GS.INIT;

    public GameContext(State state, int maxPlayers) {
        this.threadPool = Executors.newFixedThreadPool(maxPlayers);
        this.maxPlayers = maxPlayers;
        this.state = state;
        players = new ArrayList<>();
    }

    /**
     * Register a player to game context. Returns player ID which is used in all subsequent calls to this Game Context.
     *
     * @param client
     */
    public synchronized void registerPlayer(IPlayer client) {
        if (maxPlayers == players.size())
            throw new IllegalStateException("Already at max players");
        players.add(client);
    }

    public synchronized List<IPlayer> getPlayers() {
        return players;
    }


    /**
     * Runs one iteration of the game and signals all parties new game state.
     * It's only possible to run this if game is currently playing.
     */
    public synchronized void play() throws Exception {

        if (gamestate != GS.INIT)
            throw new IllegalStateException("Impossible to iterate if we're not playing");
        gamestate = GS.PLAY;

        try {
            while (!state.isFinal()) {
                logger.debug("Current State: " + state.toJSONObject().toString());
                List<Future<Action>> actionsF = new ArrayList<>();
                for (IPlayer cl : players) {
                    actionsF.add(this.threadPool.submit(() -> cl.signalNewState(state)));
                }

                List<Action> actions = new ArrayList<>();
                for (int i = 0; i < players.size(); ++i) {
                    try {
                        actions.add(actionsF.get(i).get());
                    } catch (ExecutionException e) {
                        Exception ex = (Exception) e.getCause();
                        logger.error(players.get(i).getName(), ex);
                        players.get(i).sendError("[ERROR]:" + ex.toString());
                        throw  ex;
                    }
                }

                state = state.nextState(actions);
            }

            logger.debug("Final state: " + state.toString());
            if (state.isFinal()) {
                for (IPlayer cl : players)
                    cl.signalCompleted("Game Finished. We have a winner");
            }
            threadPool.shutdown();
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        } finally {
            close();
        }
    }

    @Override
    public void close() throws Exception {
        this.gamestate = GS.STOP;
        this.threadPool.shutdown();
        for (IPlayer player : players) {
            try {
                player.close();
            } catch (Exception ignorable) {}
        }
    }

    public static enum GS {INIT, STOP, PLAY}
}
