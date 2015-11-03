package hr.best.ai.gl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.log4j.Logger;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main class which surrounds whole play of one game. Game can be in one of
 * three states, contained in enum GS. Those are INIT, PLAY and STOP. Initially
 * game starts in INIT state and waits for players to connect and register. When
 * someone calls {#startGame} method game context moves to PLAY state and cannot
 * move back to initial state. In it dedicated thread is periodically checking
 * what's going on with the game, stopping it entirely if necessary(for example
 * players fail to respond under given time limit). From PLAY game state can
 * move to STOP. From PAUSE it's possible to come back to PLAY, however once
 * STOP there's no coming back.
 */
public class GameContext implements AutoCloseable {

	final static Logger logger = Logger.getLogger(GameContext.class);
	private final List<AbstractPlayer> players = new ArrayList<>();
	private final List<NewStateObserver> observers = new ArrayList<>();
	private final int maxPlayers;
    private final int minPlayers;

	private State state;
	private GS gamestate = GS.INIT;

	public GameContext(State state, int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.state = state;
    }

    public GameContext(State state, int noPlayers) {
        this(state, noPlayers, noPlayers);
    }

	/**
	 * Register a player to game context. Returns player ID which is used in all
	 * subsequent calls to this Game Context.
	 *
	 * @param client
	 */
	public synchronized void addPlayer(AbstractPlayer client) {
		if (gamestate != GS.INIT)
			throw new IllegalStateException(
					"Game must be in initialization state");

		if (maxPlayers == players.size())
			throw new IllegalStateException("Already at max players");
		players.add(client);
	}

	public synchronized void addObserver(NewStateObserver observer) {
		if (gamestate != GS.INIT)
			throw new IllegalStateException(
					"Game must be in initialization state");
		this.observers.add(observer);
	}

	public synchronized List<AbstractPlayer> getPlayers() {
		return players;
	}

	/**
	 * runs the whole game logic.
	 */
	public synchronized void play() throws Exception {
        if (players.size() < this.minPlayers || players.size() > this.maxPlayers)
            throw new InvalidStateException(
                    "Invalid number of player. Expected in range ["
                            + minPlayers + ", "
                            + maxPlayers + "] got: " + players.size());

		if (gamestate != GS.INIT)
			throw new IllegalStateException(
					"Impossible to iterate if we're not playing");
		gamestate = GS.PLAY;

		ExecutorService threadPool = Executors.newFixedThreadPool(observers
				.size() + players.size());
		
		try {
			while (!state.isFinal()) {
				List<Future<Action>> actionsF = new ArrayList<>();
				for (int i = 0; i < players.size(); ++i) {
					
					logger.debug("Current State: "
							+ state.toJSONObjectAsPlayer(i)
									.toString());

                    final int playerNo = i + 1; // TODO Make state player beginning from zero
					actionsF.add(threadPool.submit(() -> state.parseAction(players
									.get(playerNo - 1) // TODO fix this monstrosity hack
									.signalNewState(
                                            state.toJSONObjectAsPlayer(playerNo)))
					));
				}

				observers.forEach(cl -> threadPool.submit(() -> cl
						.signalNewState(state)));

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
						throw ex;
					}
				}

                long t = System.currentTimeMillis();
				state = state.nextState(actions);
                logger.debug(String.format("Calculating new state finished [%3dms]", System.currentTimeMillis() - t));
			}
			logger.debug("Final state: " + state.toString());
		} catch (Exception ex) {
			logger.error(ex);
			throw ex;
		} finally {
			threadPool.shutdown();
			close();
		}
	}

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

	public static enum GS {
		INIT, STOP, PLAY
	}
}
