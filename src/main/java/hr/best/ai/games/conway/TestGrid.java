package hr.best.ai.games.conway;

import com.google.gson.JsonObject;
import hr.best.ai.games.conway.gamestate.Cell;
import hr.best.ai.games.conway.gamestate.ConwayGameState;
import hr.best.ai.games.conway.gamestate.Rulesets;
import hr.best.ai.games.conway.visualization.ConwayUtilities;
import hr.best.ai.games.conway.visualization.GameBarPanel;
import hr.best.ai.games.conway.visualization.GameGridWithActionsPanel;
import hr.best.ai.games.conway.visualization.PlayerInfoPanel;
import hr.best.ai.gl.NewStateObserver;
import hr.best.ai.server.ConfigUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Conway game runner with player input. It loads configuration from command line, like other runGames. With it rules
 * are initialized. Controls are simple -> Left mouse button (BUTTON1) for adding P1 cell, Right mouse button
 * (BUTTON3) for adding P2 cell in next turn. Selection can be cleared with middle mouse button (BUTTON2).
 * <p>
 * Press ENTER to iterate into further game state.
 *
 * @author Neven Miculinic
 */
public class TestGrid extends JPanel {

    /**
     * Sets up initial state and visualization and listens for player input.
     * Reads desired config file in args or the default one if no path is provided.
     * 
     * 
     * @param args
     *            path to config file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Rulesets.getInstance(); // loading the class static part into JVM
        JsonObject config = ConfigUtilities.configFromCMDArgs(args);

        final StateWrapper stateWrapper = new StateWrapper();

        stateWrapper.state = (ConwayGameState) ConfigUtilities.genInitState(config);

        SwingUtilities.invokeAndWait(() -> {
            PlayerInfoPanel p1info = ConwayUtilities.getP1DefaultInfoPanel("Player 1");
            PlayerInfoPanel p2info = ConwayUtilities.getP2DefaultInfoPanel("Player 2");
            GameBarPanel bar = ConwayUtilities.getDefaultGameBarPanel(stateWrapper.state);

            final GameGridWithActionsPanel grid = ConwayUtilities.getGameGridWithActionsPanel(stateWrapper.state);

            grid.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Cell clickedCell = grid.fromPoint(e.getPoint());
                    switch (e.getButton()) {
                        case MouseEvent.BUTTON1:
                            grid.getP1Actions().add(clickedCell);
                            grid.getP2Actions().remove(clickedCell);
                            break;
                        case MouseEvent.BUTTON2:
                            grid.getP1Actions().remove(clickedCell);
                            grid.getP2Actions().remove(clickedCell);
                            break;
                        case MouseEvent.BUTTON3:
                            grid.getP1Actions().remove(clickedCell);
                            grid.getP2Actions().add(clickedCell);
                            break;
                        default:
                            System.err.println(e.getButton());
                            break;
                    }
                    grid.repaint();
                }
            });

            final List<NewStateObserver> GUIObservers = Arrays.asList(grid, p1info, p2info, bar);

            final JFrame frame = ConwayUtilities.composeVisualization(
                    new Dimension(1280, 800)
                    , grid
                    , p1info
                    , p2info
                    , bar
            );

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER:
                            stateWrapper.state = stateWrapper.state.nextState(Arrays.asList(
                                    grid.getP1Actions()
                                    , grid.getP2Actions()
                            ));
                            grid.getP1Actions().clear();
                            grid.getP2Actions().clear();
                            GUIObservers.forEach(x -> x.signalNewState(stateWrapper.state));
                            System.out.println("Player 1 has " + stateWrapper.state.getP1Remainingcells() + " cells " +
                                    "left.");
                            System.out.println("Player 2 has " + stateWrapper.state.getP2Remainingcells() + " cells " +
                                    "left.");
                            break;
                        default:
                            break;
                    }
                }
            });
        });
    }

    /**
     * Simple wrapper around game state to make possible to have final reference
     * to game state
     */
    private static class StateWrapper {
        public volatile ConwayGameState state;
    }
}
