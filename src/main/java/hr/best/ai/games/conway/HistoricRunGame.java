package hr.best.ai.games.conway;

import com.google.gson.JsonObject;

import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.games.conway.visualization.HistoricFrame;
import hr.best.ai.gl.*;

import hr.best.ai.server.ConfigUtilities;
import org.apache.log4j.Logger;


import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
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
        final List<ConwayGameState> stanje = new ArrayList<>();

        try(GameContext gc = new GameContext(initialState, 2)) {
            players.forEach(gc::addPlayer);
            logger.debug("Starting game simulation");
            gc.addObserver(new NewStateObserver() {
                @Override
                public void signalNewState(State state) {
                    stanje.add((ConwayGameState) state);
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


                SwingUtilities.invokeLater(()->{
                    HistoricFrame hf=new HistoricFrame(stanje);
                    hf.setSize(new Dimension(1000,800));
                    //hf.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    hf.setVisible(true);
                });

            }
        }
    }
