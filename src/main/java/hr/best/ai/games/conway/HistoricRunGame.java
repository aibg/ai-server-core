package hr.best.ai.games.conway;

import com.google.gson.JsonObject;

import hr.best.ai.games.conway.gamestate.Cells;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.games.conway.visualization.*;
import hr.best.ai.gl.*;

import hr.best.ai.server.ConfigUtilities;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;


import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simulates the game and then allows for moving back and forth through the states. 
 * Press 'K' for next state and 'J' for the state before.
 */
public class HistoricRunGame extends JPanel {

    final static Logger logger = Logger.getLogger(HistoricRunGame.class);

    /**
     * Sets up initial state and game context and simulates the game. Then it brings up 
     * visualization which allows user to view its 'history'.
     * Reads desired config file in args or the default one if no path is provided.
     * 
     * @param args
     *            path to config file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        JsonObject config = ConfigUtilities.configFromCMDArgs(args);

        final List<AbstractPlayer> players = ConfigUtilities.istantiateAllPlayersFromConfig(
                config.getAsJsonArray("players")
                , config.get("port").getAsInt()
        );

        ConwayGameState initialState = (ConwayGameState) ConfigUtilities.genInitState(config);
        final List<ConwayGameState> stateList = Collections.synchronizedList(new ArrayList<>());
        final List<Pair<Cells, Cells>> playerActions = Collections.synchronizedList(new ArrayList<>());

        // Simulate the game
        try(GameContext gc = new GameContext(initialState, 2)) {
            players.forEach(gc::addPlayer);
            logger.debug("Starting game simulation");
            
            // adds observer for saving states
            gc.addObserver(new NewStateObserver() {
                @Override
                public void signalNewState(State state) {
                    final ConwayGameState st = (ConwayGameState) state;
                    stateList.add(st);
                    playerActions.add(Pair.of(
                            st.getPlayer1Actions()
                            , st.getPlayer2Actions()
                    ));
                }

                @Override
                public void signalError(JsonObject message) {
                }

                @Override
                public String getName() {
                    return "historic propagation";
                }

                @Override
                public void close() throws Exception {
                }
            });
            gc.play();

            // Removing first actions since they are always empty due to how actions are logged
            // (new state only contains action which were instructed to previous state)
            playerActions.remove(0);

            // on last turn there are no actions
            playerActions.add(Pair.of(new Cells(), new Cells()));

            SwingUtilities.invokeAndWait(() -> {
                GameGridWithActionsPanel grid = ConwayUtilities.getGameGridWithActionsPanel(initialState);
                grid.setP1Actions(playerActions.get(0).getLeft());
                grid.setP2Actions(playerActions.get(0).getRight());

                PlayerInfoPanel p1info = ConwayUtilities.getP1DefaultInfoPanel(players.get(0).getName());
                PlayerInfoPanel p2info = ConwayUtilities.getP2DefaultInfoPanel(players.get(1).getName());
                GameBarPanel bar = ConwayUtilities.getDefaultGameBarPanel(initialState);

                final List<NewStateObserver> GUIObservers = Arrays.asList(grid, p1info, p2info, bar);

                final JFrame frame = ConwayUtilities.composeVisualization(
                        new Dimension(1280, 800)
                        , grid
                        , p1info
                        , p2info
                        , bar
                );

                frame.addKeyListener(new KeyAdapter() {
                    int currState = 0;

                    @Override
                    public void keyPressed(KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_K:
                                currState = Math.max(0, currState - 1);
                                break;
                            case KeyEvent.VK_J:
                                currState = Math.min(stateList.size() - 1, currState + 1);
                                break;
                            default:
                                break;
                        }

                        grid.setP1Actions(playerActions.get(currState).getLeft());
                        grid.setP2Actions(playerActions.get(currState).getRight());
                        GUIObservers.forEach(x -> x.signalNewState(stateList.get(currState)));
                    }
                });
            });
        }
    }
}
