package hr.best.ai.gl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hr.best.ai.exceptions.AIBGExceptions;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main class which surrounds whole play of one game. Game can be in one of
 * three states, contained in enum GS. Those are INIT, PLAY and STOP. Initially
 * game starts in INIT state and waits for players to connect and register. When
 * someone calls {#startGame} method game context moves to PLAY state and cannot
 * move back to initial state. In it dedicated thread is periodically checking
 * what's going on with the game, stopping it entirely if necessary(for example
 * players fail to respond under given time limit). From PLAY game state can
 * move to STOP. From STOP there's no coming back.
 */
public class GameContext implements AutoCloseable {

    /**
     * Three possible game states
     */
    public static enum GS {
        INIT, STOP, PLAY
    }

    final static Logger logger = Logger.getLogger(GameContext.class);

    private final List<AbstractPlayer> players = new ArrayList<>();

    /**
     * List of observers (for example, they could be visualization elements)
     */
    private final List<NewStateObserver> observers = new ArrayList<>();

    private final int maxPlayers;
    private final int minPlayers;

    private State state;
    private GS gamestate = GS.INIT;

    /**
     * Creates game context with initial state and different minimum and maximum
     * number of players required.
     *
     * @param state initial game state
     * @param minPlayers minimum number of players
     * @param maxPlayers maximum number of players
     */
    public GameContext(State state, int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.state = state;
    }

    /**
     * Creates game context with initial state and exact number of players
     * required.
     *
     * @param state initial game state
     * @param noPlayers exact number of players
     */
    public GameContext(State state, int noPlayers) {
        this(state, noPlayers, noPlayers);
    }

    /**
     * Register a player to game context. Returns player ID which is used in all
     * subsequent calls to this Game Context. Allowed only in INIT state.
     *
     * @param client player you wish to register
     * @throws IllegalStateException
     *             if trying to add more players than allowed or not in INIT
     *             state
     */
    public synchronized void addPlayer(AbstractPlayer client) {
        if (gamestate != GS.INIT)
            throw new IllegalStateException(
                    "Game must be in initialization state");
        if (maxPlayers == players.size())
            throw new IllegalStateException("Already at max players");
        players.add(client);
    }

    /**
     * Registers an observer to game context.Allowed only in INIT state.
     *
     * @param observer
     * @throws IllegalStateException
     *             if not in INIT state
     */
    public synchronized void addObserver(NewStateObserver observer) {
        if (gamestate != GS.INIT)
            throw new IllegalStateException(
                    "Game must be in initialization state");
        this.observers.add(observer);
    }

    /**
     * @return list of players
     */
    public synchronized List<AbstractPlayer> getPlayers() {
        return players;
    }

    /**
     * Runs the game. On every iteration sends game states to players, receives
     * their actions, measures how long it took and sends states to observers.
     * Stops when final state is reached.
     */
    public synchronized void play() throws Exception {
        verifyPlayerCount();
        verifyGameState();
        gamestate = GS.PLAY;

        ExecutorService threadPool = Executors.newFixedThreadPool(observers
                .size() + players.size());

        try {
            while (!state.isFinal()) {
                long turnStartTime = System.currentTimeMillis();

                List<Future<Action>> actionsF = genPlayerActions(threadPool);
                List<Future<Void>> observersF = signalObservers(threadPool);

                awaitObservers(observersF);
                List<Action> actions = awaitPlayerActionsWithErrorChecking(actionsF);

                long stateComputationStartTime = System.currentTimeMillis();

                state = state.nextState(actions);

                logger.debug(String.format(
                        "Calculating new state finished [%3dms]",
                        System.currentTimeMillis() - stateComputationStartTime)
                );
                logger.debug(String.format(
                        "Whole State cycle finished: [%3d ms]",
                        System.currentTimeMillis() - turnStartTime)
                );
            }
            logger.debug("Final state: " + state.toJSONObject().toString());
            /**
             * One last update to observers. They should query whether isFinal state
             * and than determine what they'd like to do with it.
             */
            awaitObservers(signalObservers(threadPool));
            for (int i = 0; i < players.size(); ++i) {
                final int playerNo = i;
                threadPool.submit(() -> {
                    try {
                        players
                                .get(playerNo)
                                .signalFinal(
                                        state.toJSONObjectAsPlayer(playerNo));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                ;
            }

        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        } finally {
            threadPool.shutdown();
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            close();
        }
    }

    private void verifyGameState() {
        if (gamestate != GS.INIT)
            throw new IllegalStateException(
                    "Impossible to iterate if we're not playing");
    }

    private void verifyPlayerCount() {
        if (players.size() < this.minPlayers || players.size() > this.maxPlayers)
            throw new IllegalStateException(
                    "Invalid number of player. Expected in range ["
                            + minPlayers + ", "
                            + maxPlayers + "] got: " + players.size());
    }

    private List<Future<Action>> genPlayerActions(ExecutorService threadPool) throws Exception {
        List<Future<Action>> actionsF = new ArrayList<>();
        for (int i = 0; i < players.size(); ++i) {

            final int playerNo = i;
            actionsF.add(threadPool.submit(() -> state.parseAction(players
                            .get(playerNo)
                            .signalNewState(
                                    state.toJSONObjectAsPlayer(playerNo)))
            ));
        }
        return actionsF;
    }

    private List<Action> awaitPlayerActionsWithErrorChecking(List<Future<Action>> actionsF) throws Exception {
        final ArrayList<Pair<Integer, Exception>> playerErrors = new ArrayList<>();
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < players.size(); ++i) {
            try {
                actions.add(actionsF.get(i).get());

            } catch (ExecutionException e) {
                Exception ex = (Exception) e.getCause();
                logger.error(players.get(i).getName(), ex);
                JsonObject json = new JsonObject();
                json.add("error", new JsonPrimitive(ex.toString()));
                players.get(i).sendError(json);
                playerErrors.add(Pair.of(i, ex));
            }
        }
        checkForErrors(playerErrors);
        return actions;
    }

    private void checkForErrors(ArrayList<Pair<Integer, Exception>> playerErrors) {
        if (!playerErrors.isEmpty()) {
            logger.debug("Errors happened. Aborting!");
            throw new AIBGExceptions(playerErrors.toString());
        }
    }

    private List<Future<Void>> signalObservers(ExecutorService threadPool) {
        List<Future<Void>> observersF = new ArrayList<>();
        for (NewStateObserver observer : observers) {
            observersF.add((Future<Void>) threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    observer.signalNewState(state);
                }
            }));
        }

        return observersF;
    }

    private void awaitObservers(List<Future<Void>> observersF) throws Exception {
        for (Future<Void> action : observersF)
            action.get();
    }

    /**
     * Closes players and observers.
     */
    @Override
    public void close() throws Exception {
        this.gamestate = GS.STOP;
        for (AbstractPlayer player : players) {
            try {
                player.close();
            } catch (Exception ignorable) {
            }
        }
        for (NewStateObserver observer : observers) {
            try {
                observer.close();
            } catch (Exception ignorable) {
            }
        }
    }

}
