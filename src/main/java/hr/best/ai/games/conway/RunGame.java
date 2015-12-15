package hr.best.ai.games.conway;


import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.games.conway.visualization.ConwayUtilities;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.AbstractPlayer;
import hr.best.ai.gl.GameContext;
import hr.best.ai.server.ConfigUtilities;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.gson.JsonObject;
import org.apache.log4j.Logger;


/**
 * Primary class for server part in our architecture. Configuration is read from json file 
 * and is then set up in main method. Config file contains configuration about game(field 
 * size, rules, etc.), players(type, bot path, starting location and cell structure, time 
 * available for calculation, etc.) and determines if visualization is applied. GameContext 
 * then deals with communication with players and observers(visual elements) when the game 
 * is started.
 */
public class RunGame {

    final static Logger logger = Logger.getLogger(RunGame.class);

    /**
     * Sets up initial state and game context, adds visualization if needed, and runs the
     * game. Reads desired config file or the default one if no path is provided.
     * 
     * @param args
     *            path to config file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            Rulesets.getInstance(); // loading the class static part into JVM
            JsonObject config = ConfigUtilities.configFromCMDArgs(args);

            final List<AbstractPlayer> players = ConfigUtilities.istantiateAllPlayersFromConfig(
                    config.getAsJsonArray("players")
                    , config.get("port").getAsInt()
            );

            ConwayGameState initialState = (ConwayGameState) ConfigUtilities.genInitState(config);

            try(GameContext gc = new GameContext(initialState, 2)) {
                players.forEach(gc::addPlayer);

                if (config.get("visualization").getAsBoolean()) {
                    SwingUtilities.invokeAndWait(() -> {
                        GameGridPanel grid = ConwayUtilities.getDefaultGameGridPanel(initialState);
                        PlayerInfoPanel p1info = ConwayUtilities.getP1DefaultInfoPanel(players.get(0).getName());
                        PlayerInfoPanel p2info = ConwayUtilities.getP2DefaultInfoPanel(players.get(1).getName());
                        GameBarPanel bar = ConwayUtilities.getDefaultGameBarPanel(initialState);

                        gc.addObserver(bar);
                        gc.addObserver(grid);
                        gc.addObserver(p1info);
                        gc.addObserver(p2info);

                        final JFrame frame = ConwayUtilities.composeVisualization(
                                new Dimension(1280, 800)
                                , grid
                                , p1info
                                , p2info
                                , bar
                        );
                    });
                    Thread.sleep(1000);
                }
                gc.play();
            }

        } catch (Exception ex) {
            if (ConfigUtilities.socket != null) {
                ConfigUtilities.socket.close();
            }
            ConfigUtilities.socket = null;
            logger.error(ex);
            ex.printStackTrace();
        }
    }
}
