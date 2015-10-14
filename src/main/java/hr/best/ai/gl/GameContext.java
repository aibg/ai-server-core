package hr.best.ai.gl;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
	private final List<IPlayer> players = new ArrayList<>();
	private final List<NewStateObserver> observers = new ArrayList<>();
	private final int maxPlayers;

	private State state;
	private GS gamestate = GS.INIT;

	public GameContext(State state, int maxPlayers) {
		this.maxPlayers = maxPlayers;
		this.state = state;
	}

	/**
	 * Register a player to game context. Returns player ID which is used in all
	 * subsequent calls to this Game Context.
	 *
	 * @param client
	 */
	public synchronized void addPlayer(IPlayer client) {
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

	public synchronized List<IPlayer> getPlayers() {
		return players;
	}

	/**
	 * runs the whole game logic.
	 */
	public synchronized void play() throws Exception {

		if (gamestate != GS.INIT)
			throw new IllegalStateException(
					"Impossible to iterate if we're not playing");
		gamestate = GS.PLAY;

		ExecutorService threadPool = Executors.newFixedThreadPool(observers
				.size() + players.size());
		
		
		try {
			while (!state.isFinal()) {
				Thread.sleep(1000);
				long[] startTime=new long[players.size()];
				List<Future<Action>> actionsF = new ArrayList<Future<Action>>();
				for (int i = 0; i < players.size(); ++i) {
					
					logger.debug("Current State: "
							+ state.toJSONObjectAsPlayer(i + 1)
									.toString());
					players.get(i).getBucket().fill();
					
					final int playerNo = i;
					startTime[i]=System.currentTimeMillis();
					actionsF.add(threadPool.submit(new Callable<Action>() {
						@Override
						public Action call() throws Exception {

							return state.parseAction(players
									.get(playerNo)
									.signalNewState(
											state.toJSONObjectAsPlayer(playerNo + 1)));
						}

					}));
				}

				observers.forEach(cl -> threadPool.submit(() -> cl
						.signalNewState(state)));

				List<Action> actions = new ArrayList<>();
				for (int i = 0; i < players.size(); ++i) {
					try {
						actions.add(actionsF.get(i).get());
						players.get(i).getBucket().take(System.currentTimeMillis()-startTime[i]);
						
					} catch (ExecutionException e) {
						Exception ex = (Exception) e.getCause();
						logger.error(players.get(i).getName(), ex);
						players.get(i).sendError("[ERROR]:" + ex.toString());
						throw ex;
					}
				}

				state = state.nextState(actions);
			}

			logger.debug("Final state: " + state.toString());
			if (state.isFinal()) {
				players.forEach(cl -> cl
						.signalCompleted("Game Finished. We have a winner"));
				observers.forEach(cl -> cl
						.signalCompleted("Game Finished. We have a winner"));
			}
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
		for (IPlayer player : players) {
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
