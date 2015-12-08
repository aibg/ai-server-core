package hr.best.ai.games.conway;

import com.google.gson.JsonObject;

import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.games.conway.visualization.*;
import hr.best.ai.gl.*;

import hr.best.ai.server.ConfigUtilities;
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
 * Created by lpp on 11/8/15.
 */
public class HistoricRunGame extends JPanel {

    final static Logger logger = Logger.getLogger(HistoricRunGame.class);

    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        JsonObject config = ConfigUtilities.configFromCMDArgs(args);

        final List<AbstractPlayer> players = ConfigUtilities.istantiateAllPlayersFromConfig(
                config.getAsJsonArray("players")
                , config.get("port").getAsInt()
        );

        ConwayGameState initialState = (ConwayGameState) ConfigUtilities.genInitState(config);
        final List<ConwayGameState> stateList = Collections.synchronizedList(new ArrayList<>());
        stateList.add(initialState);
        logger.info(initialState);

        System.err.println(stateList);

        // Simulate the game
        try(GameContext gc = new GameContext(initialState, 2)) {
            players.forEach(gc::addPlayer);
            logger.debug("Starting game simulation");
            gc.addObserver(new NewStateObserver() {
                @Override
                public void signalNewState(State state) {
                    System.err.println("WTF");
                    System.err.println(stateList);
                    stateList.add((ConwayGameState) state);
                    System.err.println(stateList);
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

            System.err.println(stateList);

            SwingUtilities.invokeAndWait(() -> {
                GameGridPanel grid = ConwayUtilities.getDefaultGameGridPanel(initialState);
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

                        GUIObservers.forEach(x -> x.signalNewState(stateList.get(currState)));
                        logger.debug(stateList.get(currState));
                    }
                });
            });
        }
    }
}
